package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Variable;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskCountdown;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public final static GameHelper INSTANCE = new GameHelper();

    public TaskTimer reduceTask = TaskTimer.NONE_TASK;
    public TaskTimer barUpdateTask = TaskTimer.NONE_TASK;
    public TaskCountdown stopTask = TaskCountdown.NONE_TASK;
    public TaskCountdown startTask = TaskCountdown.NONE_TASK;
    private WorldHelper worldHelper;
    private CommandBossBar bossBar;
    private MinecraftServer server;
    private final static Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");

    private GameHelper(){
    }

    public MinecraftServer getServer(){
        return this.server;
    }

    public void setServer(MinecraftServer server){
        this.server = server;
    }

    public void initialize(){
        if (Variable.INSTANCE.progress.progress.isResetWorld()){
            Variable.INSTANCE.progress.progress = GameProgress.Progress.WAIT_PLAYER;
            setInitialProgress();
        }

        setKeepInventory(!Variable.INSTANCE.progress.progress.isDeathmatch());
        if (Variable.INSTANCE.progress.resizeLapTimer <= 0
                && Variable.INSTANCE.progress.progress != GameProgress.Progress.DEATHMATCH){
            Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
        }
        if (Variable.INSTANCE.progress.progress.isStarted()){
            resumeGame();
        }
    }

    public void onPlayerDeath(ServerPlayerEntity player){
        if (Variable.INSTANCE.progress.progress.isDeathmatch()){
            player.changeGameMode(GameMode.SPECTATOR);
            if (getInGamePlayers() == 1){
                for (ServerPlayerEntity p1 : Variable.INSTANCE.server.getPlayerManager().getPlayerList()){
                    p1.changeGameMode(GameMode.ADVENTURE);
                    p1.getInventory().clear();
                }
                ServerPlayerEntity p = getFirstPlayer();
                Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.end.broadcast", TextUtil.GOLD, p.getName()), false);

                FireworkRocketEntity firework = EntityType.FIREWORK_ROCKET.create(Variable.INSTANCE.server.getOverworld());
                if (firework != null) {
                    firework.refreshPositionAfterTeleport(p.getPos());
                }

                stopGame();
            }
        }
    }

    public ActionResult onPlayerDamaged(DamageSource source){
        if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.PEACEFUL){
            return ActionResult.FAIL;
        }
        else if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.NO_PVP){
            if (source.getSource() != null && source.getSource().isPlayer()){
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    public void prepareStartGame(){
        Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                "battlegrounds.game.already.broadcast",
                TextUtil.GREEN), false);
        startTask = new TaskCountdown(
                () -> {
                    for (ServerPlayerEntity player1 : Variable.INSTANCE.server.getPlayerManager().getPlayerList()){
                        PlayerUtil.sendTitle(player1, TextUtil.withColor(
                                Text.literal(String.valueOf(startTask.getCount())), TextUtil.GREEN));
                        player1.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                this::startGame,
                0, 20,
                Variable.INSTANCE.config.gameStartDelaySeconds
        );
        TaskScheduler.INSTANCE.runTask(startTask);
    }

    public void prepareResetWorlds(VoteHelper voter){
        Variable.INSTANCE.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                "battlegrounds.game.start.broadcast", TextUtil.GREEN, Variable.INSTANCE.config.serverCloseDelaySeconds), false);
        stopTask = new TaskCountdown(
                () -> {
                    for (ServerPlayerEntity player : Variable.INSTANCE.server.getPlayerManager().getPlayerList()){
                        PlayerUtil.sendTitle(player, TextUtil.withColor(
                                Text.literal(String.valueOf(stopTask.getCount())), TextUtil.GREEN));
                        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                () -> stopServer(TextUtil.translatableWithColor(
                        "battlegrounds.game.server.stop", TextUtil.GREEN)),
                0, 20, Variable.INSTANCE.config.serverCloseDelaySeconds
        );
        TaskScheduler.INSTANCE.runTask(stopTask);
        Variable.INSTANCE.progress.players = Arrays.stream(voter.getPlayerProfiles()).map((GameProfile profile) ->
                profile.getId().toString()).toList();
        setInitialProgress();
        Variable.INSTANCE.progress.progress = GameProgress.Progress.RESET_WORLD;
    }

    public void tryResetWorlds(){
        if (Variable.INSTANCE.progress.progress.isResetWorld()){
            if (server.getSavePath(WorldSavePath.ROOT) == null){
                Environment.LOGGER.error("无法重置地图：savePath 不能为 null");
            }
            else{
                resetWorlds();
                Environment.LOGGER.info("已重置地图");
            }
        }
    }

    private void resetWorlds(){
        Path savePath = server.getSavePath(WorldSavePath.ROOT);
        FileUtil.delete(savePath, "bg_progress.json");
    }

    public void setInitialProgress(){
        Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Variable.INSTANCE.progress.currentLap = 0;
        Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
    }

    public void setKeepInventory(boolean value){
        this.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, this.server);
    }

    public int getInGamePlayers(){
        int num = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()){
            if (player.interactionManager.getGameMode() == GameMode.SURVIVAL &&
                    Variable.INSTANCE.progress.players.contains(player.getGameProfile().getId().toString())) num++;
        }
        return num;
    }

    public ServerPlayerEntity getFirstPlayer(){
        return this.server.getPlayerManager().getPlayerList().get(0);
    }

    public void stopServer(Text message){
        CopyOnWriteArrayList<ServerPlayerEntity> players = new CopyOnWriteArrayList<>(this.server.getPlayerManager().getPlayerList());
        for (ServerPlayerEntity player : players){
            player.networkHandler.disconnect(message);
        }
        this.server.stop(false);
    }

    public void startGame(){
        World world = this.server.getOverworld();
        worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);

        for (String uuid : Variable.INSTANCE.progress.players){
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
            if (player != null) {
                PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                player.getInventory().clear();
            }
        }
        Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        Variable.INSTANCE.progress.progress = GameProgress.Progress.DEVELOP;
        runTasks();
        createBossBar();
    }

    public void resumeGame(){
        World world = this.server.getOverworld();
        worldHelper = new WorldHelper(world);
        runTasks();
        createBossBar();
    }

    private void createBossBar(){
        BossBarManager manager = this.server.getBossBarManager();
        if (manager.get(BAR_ID) != null){
            bossBar = manager.get(BAR_ID);
        }
        else{
            bossBar = manager.add(BAR_ID,
                    TextUtil.translatableWithColor("battlegrounds.border.bar",
                            TextUtil.GREEN,
                            reduceTask.getDelay() / 20)
            );
        }
        barUpdateTask = new TaskTimer(() -> {
            if (bossBar == null){
                throw new RunnableCancelledException();
            }
            bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
            bossBar.setMaxValue((int) (Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks));
            bossBar.setValue((int) reduceTask.getDelay());
            bossBar.setName(TextUtil.translatableWithColor("battlegrounds.border.bar",
                    TextUtil.GREEN,
                    reduceTask.getDelay() / 20));
        }, reduceTask.getDelay() % 20, 20);
        TaskScheduler.INSTANCE.runTask(barUpdateTask);
    }

    private void removeBossBar(){
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bar = manager.get(BAR_ID);
        if (bar != null){
            bar.clearPlayers();
            manager.remove(bar);
        }
        bossBar = null;
    }

    public void runTasks(){
        reduceTask = new TaskTimer(() -> {
            // 启用 PVP
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.pvpModeBeginBorderNum){
                Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
            }
            // 最终圈
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.resizeNum){
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.already.broadcast", TextUtil.GOLD,
                        (Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks) / 1200), false);
            }
            // 死亡竞赛
            else if (Variable.INSTANCE.progress.currentLap
                    >= Variable.INSTANCE.config.border.resizeNum){
                Variable.INSTANCE.progress.progress = GameProgress.Progress.DEATHMATCH;
                setKeepInventory(false);
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
                worldHelper.setBorderSize(Variable.INSTANCE.config.border.finalSize);
                for (String uuid : Variable.INSTANCE.progress.players){
                    ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                    if (player != null) {
                        PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                    }
                }
                removeBossBar();

                throw new RunnableCancelledException();
            }
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()){
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1);
            }
            worldHelper.setBorderSize(worldHelper.getBorderSize() - Variable.INSTANCE.config.border.resizeBlocks,
                    Variable.INSTANCE.config.border.resizeTimeTicks * 50);
            this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.border.reduce.broadcast", TextUtil.GOLD), false);
            Variable.INSTANCE.progress.currentLap++;
        }, Variable.INSTANCE.progress.resizeLapTimer,
                () -> Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks);
        TaskScheduler.INSTANCE.runTask(reduceTask);
    }

    public void stopGame(){
        Variable.INSTANCE.progress.progress = GameProgress.Progress.IDLE;
        setInitialProgress();
        worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);
    }
}
