package com.github.winexp.battlegrounds.helper;

import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.helper.task.RunnableCancelledException;
import com.github.winexp.battlegrounds.helper.task.TaskCountdown;
import com.github.winexp.battlegrounds.helper.task.TaskScheduler;
import com.github.winexp.battlegrounds.helper.task.TaskTimer;
import com.github.winexp.battlegrounds.util.*;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public final static GameHelper INSTANCE = new GameHelper();
    private final static Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");
    private final static boolean KEEP_INVENTORY = false;
    private final static Path SAVE_PATH_TMP_FILE = FabricLoader.getInstance().getGameDir().resolve(Path.of("reset_world.session"));
    public TaskTimer reduceTask = TaskTimer.NONE_TASK;
    public TaskTimer barUpdateTask = TaskTimer.NONE_TASK;
    public TaskCountdown stopTask = TaskCountdown.NONE_TASK;
    public TaskCountdown startTask = TaskCountdown.NONE_TASK;
    private WorldHelper worldHelper;
    private CommandBossBar bossBar;
    private MinecraftServer server;

    private GameHelper() {
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void initialize() {
        if (Variable.INSTANCE.progress.gameStage.isResetWorld()) {
            Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.WAIT_PLAYER;
            setInitialProgress();
        }

        setKeepInventory(KEEP_INVENTORY);
        if (Variable.INSTANCE.progress.resizeLapTimer <= 0
                && Variable.INSTANCE.progress.gameStage != GameProgress.GameStage.DEATHMATCH) {
            Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
        }
        if (Variable.INSTANCE.progress.gameStage.isStarted()) {
            resumeGame();
        }
    }

    public void onPlayerDeath(ServerPlayerEntity player) {
        if (Variable.INSTANCE.progress.gameStage.isDeathmatch()) {
            PlayerUtil.setGameMode(player, GameMode.SPECTATOR);
            if (getInGamePlayers() == 1) {
                ServerPlayerEntity p = getFirstPlayer();
                server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.end.broadcast", TextUtil.GOLD, p.getName()), false);

                Random random = p.getWorld().getRandom();
                int amount = random.nextInt(4) + 1;
                for (int i = 0; i < amount; i++) {
                    FireworkRocketEntity firework = EntityType.FIREWORK_ROCKET.create(server.getOverworld());
                    if (firework != null) {
                        Vec3d pos = p.getPos();
                        Vec3d offset = new Vec3d(
                                random.nextDouble() * (4 * 2) - 4,
                                1,
                                random.nextDouble() * (4 * 2) - 4
                        );
                        firework.refreshPositionAfterTeleport(pos.add(offset));
                        p.getWorld().spawnEntity(firework);
                    }
                }

                stopGame();
            }
        }
        PlayerUtil.setGameModeWithMap(player);
    }

    public ActionResult onPlayerDamaged(DamageSource source) {
        if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.PEACEFUL) {
            return ActionResult.FAIL;
        } else if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.NO_PVP) {
            if (source.getSource() != null && source.getSource().isPlayer()) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    public void prepareStartGame() {
        server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                "battlegrounds.game.already.broadcast",
                TextUtil.GREEN), false);
        startTask = new TaskCountdown(
                () -> {
                    for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
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

    public void prepareResetWorlds(VoteHelper voter) {
        this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                "battlegrounds.game.start.broadcast", TextUtil.GREEN, Variable.INSTANCE.config.serverCloseDelaySeconds), false);
        stopTask = new TaskCountdown(
                () -> {
                    for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                        PlayerUtil.sendTitle(player, TextUtil.withColor(
                                Text.literal(String.valueOf(stopTask.getCount())), TextUtil.GREEN));
                        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                () -> this.stopServer(TextUtil.translatableWithColor(
                        "battlegrounds.game.server.stop", TextUtil.GREEN)),
                0, 20, Variable.INSTANCE.config.serverCloseDelaySeconds
        );
        TaskScheduler.INSTANCE.runTask(stopTask);
        FileUtil.writeString(
                SAVE_PATH_TMP_FILE,
                this.server.getSavePath(WorldSavePath.ROOT).toString()
        );
        HashMap<String, String> playerList = new HashMap<>();
        for (GameProfile profile : voter.getPlayerProfiles()) {
            playerList.put(
                    profile.getId().toString(),
                    "adventure"
            );
        }
        Variable.INSTANCE.progress.players = playerList;
        this.setInitialProgress();
        Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.RESET_WORLD;
    }

    public void tryResetWorlds() {
        if (Files.isRegularFile(SAVE_PATH_TMP_FILE)) {
            resetWorlds();
            FileUtil.delete(SAVE_PATH_TMP_FILE, true);
            Environment.LOGGER.info("已重置地图");
        }
    }

    private void resetWorlds() {
        Path savePath = Path.of(FileUtil.readString(SAVE_PATH_TMP_FILE).trim());
        FileUtil.delete(savePath, false, "bg_progress.json");
    }

    public void setInitialProgress() {
        Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Variable.INSTANCE.progress.hasEffects = false;
        Variable.INSTANCE.progress.currentLap = 0;
        Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
    }

    public void setKeepInventory(boolean value) {
        this.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, this.server);
    }

    public int getInGamePlayers() {
        int num = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (player.interactionManager.getGameMode() == GameMode.SURVIVAL &&
                    Variable.INSTANCE.progress.players.containsKey(player.getGameProfile().getId().toString())) num++;
        }
        return num;
    }

    public ServerPlayerEntity getFirstPlayer() {
        return this.server.getPlayerManager().getPlayerList().get(0);
    }

    public void stopServer(Text message) {
        CopyOnWriteArrayList<ServerPlayerEntity> players = new CopyOnWriteArrayList<>(this.server.getPlayerManager().getPlayerList());
        for (ServerPlayerEntity player : players) {
            player.networkHandler.disconnect(message);
        }
        this.server.stop(false);
    }

    public void startGame() {
        World world = this.server.getOverworld();
        worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);

        for (String uuid : Variable.INSTANCE.progress.players.keySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
            if (player != null) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 15 * 20, 4));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 15 * 20, 3));

                PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                player.getInventory().clear();
                PlayerUtil.setGameMode(player, GameMode.SURVIVAL);
            }
        }
        Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.DEVELOP;
        Variable.INSTANCE.progress.hasEffects = true;
        runTasks();
        createBossBar();
    }

    public void resumeGame() {
        World world = this.server.getOverworld();
        worldHelper = new WorldHelper(world);
        runTasks();
        createBossBar();
    }

    private void createBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        if (manager.get(BAR_ID) != null) {
            bossBar = manager.get(BAR_ID);
        } else {
            bossBar = manager.add(BAR_ID,
                    TextUtil.translatableWithColor("battlegrounds.border.bar",
                            TextUtil.GREEN,
                            reduceTask.getDelay() / 20)
            );
        }
        barUpdateTask = new TaskTimer(() -> {
            if (bossBar == null) {
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

    private void removeBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bar = manager.get(BAR_ID);
        if (bar != null) {
            bar.clearPlayers();
            manager.remove(bar);
        }
        bossBar = null;
    }

    public void runTasks() {
        this.reduceTask = new TaskTimer(() -> {
            // 启用 PVP
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.pvpModeBeginBorderNum) {
                Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                Variable.INSTANCE.progress.hasEffects = false;
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
                worldHelper.setBorderSize(worldHelper.getBorderSize() - Variable.INSTANCE.config.border.resizeBlocks,
                        Variable.INSTANCE.config.border.resizeTimeTicks * 50);
            }
            // 最终圈
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.finalBorderNum) {
                worldHelper.setBorderSize(worldHelper.getBorderSize() - Variable.INSTANCE.config.border.resizeBlocks,
                        Variable.INSTANCE.config.border.resizeTimeTicks * 50);
            }
            // 死亡竞赛-提示
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.deathmatchBeginBorderNum) {
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.already.broadcast", TextUtil.GOLD,
                        (Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks) / 1200), false);
            }
            // 死亡竞赛-初始圈
            if (Variable.INSTANCE.progress.currentLap
                    == Variable.INSTANCE.config.border.deathmatchBeginBorderNum) {
                Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.DEATHMATCH;
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.deathmatch.start.broadcast", TextUtil.GOLD), false);
                this.worldHelper.setBorderSize(Variable.INSTANCE.config.border.deathmatch.initialSize);
                for (String uuid : Variable.INSTANCE.progress.players.keySet()) {
                    ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(UUID.fromString(uuid));
                    if (player != null) {
                        PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                    }
                }
                worldHelper.setBorderSize(Variable.INSTANCE.config.border.deathmatch.finalSize,
                        Variable.INSTANCE.config.border.deathmatch.resizeDelayTicks * 50);
            }
            // 死亡竞赛-最终圈
            if (Variable.INSTANCE.progress.currentLap
                    >= Variable.INSTANCE.config.border.resizeNum) {
                this.removeBossBar();
                throw new RunnableCancelledException();
            }
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 1);
            }
            this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                    "battlegrounds.game.border.reduce.broadcast", TextUtil.GOLD), false);
            Variable.INSTANCE.progress.currentLap++;
        }, Variable.INSTANCE.progress.resizeLapTimer,
                () -> Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks);
        TaskScheduler.INSTANCE.runTask(this.reduceTask);
    }

    public void stopGame() {
        for (ServerPlayerEntity player : Variable.INSTANCE.server.getPlayerManager().getPlayerList()) {
            PlayerUtil.setGameMode(player, GameMode.ADVENTURE);
            player.getInventory().clear();
        }

        Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.IDLE;
        setInitialProgress();
        worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);
    }
}
