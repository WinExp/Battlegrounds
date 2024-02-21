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
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.scoreboard.Team;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameHelper {
    public final static GameHelper INSTANCE = new GameHelper();
    private final String healthModifierId = "game.health_modifier";
    private final static Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");
    private final static Path SAVE_PATH_TMP_FILE = FabricLoader.getInstance().getGameDir().resolve(Path.of("reset_world.session"));
    private final static boolean KEEP_INVENTORY = false;
    private TeamHelper teamHelper;
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
        this.teamHelper = new TeamHelper(this.server.getScoreboard());
    }

    public void initialize() {
        if (Variable.INSTANCE.progress.gameStage.isResetWorld()) {
            Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.WAIT_PLAYER;
            this.setInitialProgress();
        }

        this.setKeepInventory(KEEP_INVENTORY);
        if (Variable.INSTANCE.progress.resizeLapTimer <= 0
                && Variable.INSTANCE.progress.gameStage != GameProgress.GameStage.DEATHMATCH) {
            Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
        }
        if (Variable.INSTANCE.progress.gameStage.isStarted()) {
            this.resumeGame();
        }
    }

    public void onPlayerRespawn(ServerPlayerEntity player) {
        if (Variable.INSTANCE.progress.gameStage.isStarted()) {
            PlayerUtil.setGameModeWithMap(player);
        }
    }

    public void onPlayerDeath(ServerPlayerEntity player) {
        if (Variable.INSTANCE.progress.gameStage.isStarted()) {
            PlayerUtil.setGameModeMap(player, GameMode.SPECTATOR);
            if (this.getInGamePlayersNum() == 1) {
                ServerPlayerEntity p = this.getLastInGamePlayer();
                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.end.broadcast", TextUtil.GOLD, p.getName()), false);

                Random random = p.getWorld().getRandom();
                int amount = random.nextInt(4) + 1;
                for (int i = 0; i < amount; i++) {
                    FireworkRocketEntity firework = EntityType.FIREWORK_ROCKET.create(this.server.getOverworld());
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

                this.stopGame();
            }
            this.server.getPlayerManager().getWhitelist().remove(player.getGameProfile());
        }
    }

    public ActionResult onPlayerDamaged(DamageSource source, ServerPlayerEntity player) {
        if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.PEACEFUL) {
            return ActionResult.FAIL;
        } else if (Variable.INSTANCE.progress.pvpMode == GameProgress.PVPMode.NO_PVP) {
            if (source.getSource() != null && source.getSource().isPlayer()) {
                return ActionResult.FAIL;
            }
        }
        if (source.getSource() != null && source.getSource().isPlayer()) {
            ServerPlayerEntity attacker = (ServerPlayerEntity) source.getSource();
            attacker.isTeammate(player);
        }
        return ActionResult.PASS;
    }

    public void prepareStartGame() {
        this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                "battlegrounds.game.already.broadcast",
                TextUtil.GREEN), false);
        this.startTask = new TaskCountdown(
                () -> {
                    for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
                        PlayerUtil.sendTitle(player1, TextUtil.withColor(
                                Text.literal(String.valueOf(startTask.getCount())), TextUtil.GREEN));
                        player1.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                () -> this.startGame(false),
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
        HashMap<UUID, GameProgress.PlayerPermission> playerList = new HashMap<>();
        for (GameProfile profile : voter.getPlayerProfiles()) {
            GameProgress.PlayerPermission permission = new GameProgress.PlayerPermission();
            permission.gameMode = GameMode.ADVENTURE;
            permission.inGame = true;
            playerList.put(
                    profile.getId(),
                    permission
            );
        }
        Variable.INSTANCE.progress.players = playerList;
        this.setInitialProgress();
        Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.RESET_WORLD;
    }

    public void tryResetWorlds() {
        if (Files.isRegularFile(SAVE_PATH_TMP_FILE)) {
            this.resetWorlds();
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
        Variable.INSTANCE.progress.currentLap = 0;
        Variable.INSTANCE.progress.resizeLapTimer = Variable.INSTANCE.config.border.resizeDelayTicks;
    }

    public void setKeepInventory(boolean value) {
        this.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, this.server);
    }

    public int getInGamePlayersNum() {
        int num = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (Variable.INSTANCE.progress.players.get(PlayerUtil.getUUID(player)).inGame) num++;
        }
        return num;
    }

    public ServerPlayerEntity getLastInGamePlayer() {
        ServerPlayerEntity result = null;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (Variable.INSTANCE.progress.players.get(PlayerUtil.getUUID(player)).inGame) {
                result = player;
            }
        }
        return result;
    }

    public void stopServer(Text message) {
        CopyOnWriteArrayList<ServerPlayerEntity> players = new CopyOnWriteArrayList<>(this.server.getPlayerManager().getPlayerList());
        for (ServerPlayerEntity player : players) {
            player.networkHandler.disconnect(message);
        }
        this.server.stop(false);
    }

    private void assignTeams() {
        String[] teams = new String[] { "Team_A", "Team_B" };
        for (String teamName : teams) {
            Team team = this.teamHelper.addTeam(teamName);
            team.setFriendlyFireAllowed(false);
        }
        List<ServerPlayerEntity> players = Variable.INSTANCE.progress.players.keySet().stream().map((uuid) ->
                this.server.getPlayerManager().getPlayer(uuid)).toList();
        this.teamHelper.assignPlayers(players);
    }

    public void startGame(boolean assignTeam) {
        World world = this.server.getOverworld();
        this.worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        this.worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        this.worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);
        this.server.getPlayerManager().setWhitelistEnabled(true);
        if (assignTeam) {
            this.teamHelper.setMaxPlayers(1);
            this.assignTeams();
        }

        for (UUID uuid : Variable.INSTANCE.progress.players.keySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attribute != null;
                EffectUtil.addAttribute(attribute, healthModifierId, Variable.INSTANCE.config.attributes.genericAdditionHealth, EntityAttributeModifier.Operation.ADDITION);
                player.setHealth(player.getMaxHealth());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 15 * 20, 3));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 15 * 20, 3));
                Variable.INSTANCE.progress.players.get(uuid).hasEffects = true;

                PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                player.getInventory().clear();
                PlayerUtil.setGameMode(player, GameMode.SURVIVAL);
            }
        }
        Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        Variable.INSTANCE.progress.gameStage = GameProgress.GameStage.DEVELOP;
        this.runTasks();
        this.createBossBar();
    }

    public void resumeGame() {
        World world = this.server.getOverworld();
        this.worldHelper = new WorldHelper(world);
        this.runTasks();
        this.createBossBar();
    }

    private void createBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        if (manager.get(BAR_ID) != null) {
            this.bossBar = manager.get(BAR_ID);
        } else {
            this.bossBar = manager.add(BAR_ID,
                    TextUtil.translatableWithColor("battlegrounds.border.bar",
                            TextUtil.GREEN,
                            this.reduceTask.getDelay() / 20)
            );
        }
        this.barUpdateTask = new TaskTimer(() -> {
            if (this.bossBar == null) {
                throw new RunnableCancelledException();
            }
            this.bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
            this.bossBar.setMaxValue((int) (Variable.INSTANCE.config.border.resizeDelayTicks + Variable.INSTANCE.config.border.resizeTimeTicks));
            this.bossBar.setValue((int) this.reduceTask.getDelay());
            this.bossBar.setName(TextUtil.translatableWithColor("battlegrounds.border.bar",
                    TextUtil.GREEN,
                    this.reduceTask.getDelay() / 20));
        }, this.reduceTask.getDelay() % 20, 20);
        TaskScheduler.INSTANCE.runTask(barUpdateTask);
    }

    private void removeBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bar = manager.get(BAR_ID);
        if (bar != null) {
            bar.clearPlayers();
            manager.remove(bar);
        }
        this.bossBar = null;
    }

    public void runTasks() {
        this.reduceTask = new TaskTimer(() -> {
            // 启用 PVP
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.pvpModeBeginBorderNum) {
                Variable.INSTANCE.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                for (UUID uuid : Variable.INSTANCE.progress.players.keySet()) {
                    Variable.INSTANCE.progress.players.get(uuid).hasEffects = false;
                }

                this.server.getPlayerManager().broadcast(TextUtil.translatableWithColor(
                        "battlegrounds.game.pvp.enable.broadcast", TextUtil.GOLD), false);
                this.worldHelper.setBorderSize(worldHelper.getBorderSize() - Variable.INSTANCE.config.border.resizeBlocks,
                        Variable.INSTANCE.config.border.resizeTimeTicks * 50);
            }
            // 最终圈
            if (Variable.INSTANCE.progress.currentLap + 1
                    == Variable.INSTANCE.config.border.finalBorderNum) {
                this.worldHelper.setBorderSize(worldHelper.getBorderSize() - Variable.INSTANCE.config.border.resizeBlocks,
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
                for (UUID uuid : Variable.INSTANCE.progress.players.keySet()) {
                    ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        player.clearStatusEffects();
                        PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                        player.setHealth(player.getMaxHealth());
                    }
                }
                this.worldHelper.setBorderSize(Variable.INSTANCE.config.border.deathmatch.finalSize,
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
        this.setInitialProgress();
        this.removeBossBar();
        this.reduceTask.cancel();
        this.server.getPlayerManager().setWhitelistEnabled(false);
        this.worldHelper.setBorderSize(Variable.INSTANCE.config.border.initialSize);
        for (UUID uuid : Variable.INSTANCE.progress.players.keySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attribute != null;
                EffectUtil.removeAttribute(attribute, this.healthModifierId);
            }
        }
        ConfigUtil.saveConfig(Variable.INSTANCE.server.getSavePath(WorldSavePath.ROOT), "bg_progress", new GameProgress());
    }
}
