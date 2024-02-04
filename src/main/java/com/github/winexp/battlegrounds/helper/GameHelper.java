package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.TextUtil;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public TaskTimer reduceTask = TaskTimer.NONE_TASK;
    public TaskTimer barUpdateTask = TaskTimer.NONE_TASK;
    private WorldHelper worldHelper;
    private CommandBossBar bossBar;
    private MinecraftServer server;
    private final static Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");

    public GameHelper(){
    }

    public MinecraftServer getServer(){
        return this.server;
    }

    public void setServer(MinecraftServer server){
        this.server = server;
    }

    public void resetWorlds(){
        Path savePath = server.getSavePath(WorldSavePath.ROOT);
        FileUtil.delete(savePath, "bg_progress.json");
    }

    public void setInitialProgress(){
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Battlegrounds.progress.currentLap = 0;
        Battlegrounds.progress.resizeLapTimer = Battlegrounds.config.border.resizeDelayTicks;
    }

    public void setKeepInventory(boolean value){
        this.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, this.server);
    }

    public int getInGamePlayers(){
        int num = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()){
            if (player.interactionManager.getGameMode() == GameMode.SURVIVAL && Battlegrounds.progress.players.contains(player.getGameProfile().getId().toString())) num++;
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
        worldHelper.setBorderSize(Battlegrounds.config.border.initialSize);

        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        for (String uuid : Battlegrounds.progress.players){
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
            if (player != null) {
                PlayerUtil.randomTeleport(this.server.getOverworld(), player);
            }
        }
        Battlegrounds.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        Battlegrounds.progress.progress = GameProgress.Progress.DEVELOP;
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
            bossBar.setMaxValue((int) (Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks));
            bossBar.setValue((int) reduceTask.getDelay());
            bossBar.setName(TextUtil.translatableWithColor("battlegrounds.border.bar",
                    TextUtil.GREEN,
                    reduceTask.getDelay() / 20));
        }, reduceTask.getDelay() % 20, 20);
        Battlegrounds.taskScheduler.runTask(barUpdateTask);
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
            if (Battlegrounds.progress.currentLap + 1
                    == Battlegrounds.config.pvpModeBeginBorderNum){
                Battlegrounds.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
            }
            // 最终圈
            if (Battlegrounds.progress.currentLap + 1
                    == Battlegrounds.config.border.resizeNum){
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.already.broadcast", TextUtil.GOLD,
                        (Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks) / 1200), false);
            }
            // 死亡竞赛
            else if (Battlegrounds.progress.currentLap
                    >= Battlegrounds.config.border.resizeNum){
                Battlegrounds.progress.progress = GameProgress.Progress.DEATHMATCH;
                setKeepInventory(false);
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
                worldHelper.setBorderSize(Battlegrounds.config.border.finalSize);
                for (String uuid : Battlegrounds.progress.players){
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
            worldHelper.setBorderSize(worldHelper.getBorderSize() - Battlegrounds.config.border.resizeBlocks,
                    Battlegrounds.config.border.resizeTimeTicks * 50);
            this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.border.reduce.broadcast", TextUtil.GOLD), false);
            Battlegrounds.progress.currentLap++;
        }, Battlegrounds.progress.resizeLapTimer,
                () -> Battlegrounds.config.border.resizeDelayTicks + Battlegrounds.config.border.resizeTimeTicks);
        Battlegrounds.taskScheduler.runTask(reduceTask);
    }

    public void stopGame(){
        Battlegrounds.progress.progress = GameProgress.Progress.IDLE;
        setInitialProgress();
        worldHelper.setBorderSize(Battlegrounds.config.border.initialSize);
    }
}
