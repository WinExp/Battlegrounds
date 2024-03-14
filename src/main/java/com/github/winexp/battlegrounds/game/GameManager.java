package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.helper.TeamHelper;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.task.LimitRepeatTask;
import com.github.winexp.battlegrounds.task.TaskCancelledException;
import com.github.winexp.battlegrounds.task.TaskExecutor;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.util.*;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.Formatting;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameManager {
    public static final GameManager INSTANCE = new GameManager();
    private final String healthModifierId = "game/health_modifier";
    private static final Identifier BAR_ID = new Identifier("battlegrounds", "progress_bar");
    private static final boolean KEEP_INVENTORY = false;
    private TeamHelper teamHelper;
    public RepeatTask reduceTask = RepeatTask.NONE_TASK;
    public RepeatTask barUpdateTask = RepeatTask.NONE_TASK;
    public LimitRepeatTask stopTask = LimitRepeatTask.NONE_TASK;
    public LimitRepeatTask startTask = LimitRepeatTask.NONE_TASK;
    private WorldHelper worldHelper;
    private CommandBossBar bossBar;
    private MinecraftServer server;

    private GameManager() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::onLivingEntityDeath);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        this.teamHelper = new TeamHelper(this.server.getScoreboard());
    }

    public void initialize() {
        if (Variables.progress.gameStage.isResetWorld()) {
            Variables.progress.gameStage = GameProgress.GameStage.WAIT_PLAYER;
            this.setInitialProgress();
        }

        this.setKeepInventory(KEEP_INVENTORY);
        if (Variables.progress.resizeLapTimer <= 0
                && Variables.progress.gameStage != GameProgress.GameStage.DEATHMATCH) {
            Variables.progress.resizeLapTimer = Variables.config.border.time.resizeDelayTicks;
        }
        if (Variables.progress.gameStage.isStarted()) {
            this.resumeGame();
        }
    }

    private void tick(MinecraftServer server) {
        for (UUID uuid : Variables.progress.players.keySet()) {
            GameProgress.PlayerPermission permission = Variables.progress.players.get(uuid);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null || !permission.inGame || !permission.hasEffects) continue;
            this.addEffects(player);
        }
    }

    private void addEffects(LivingEntity livingEntity) {
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2, 0));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 1));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2, 1));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 2, 1));
    }

    public boolean onLivingEntityDeath(LivingEntity entity, DamageSource source, float damageAmount) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return true;
        }
        if (Variables.progress.gameStage.isStarted()
                && Variables.progress.isInGame(PlayerUtil.getUUID(player))) {
            PlayerUtil.setGameModeMap(player, GameMode.SPECTATOR);
            GameProgress.PlayerPermission permission = new GameProgress.PlayerPermission();
            Variables.progress.players.put(PlayerUtil.getUUID(player), permission);
            if (this.getInGamePlayersNum() != 1) {
                return true;
            }
            ServerPlayerEntity p = this.getLastInGamePlayer();
            this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.end.broadcast", p.getDisplayName())
                    .formatted(Formatting.GOLD), false);
            Random random = p.getWorld().getRandom();
            int amount = random.nextInt(4) + 1;
            for (int i = 0; i < amount; i++) {
                int fireworkOffset = 4;
                FireworkRocketEntity firework = EntityTypes.FIREWORK_ROCKET.create(this.server.getOverworld());
                assert firework != null;
                Vec3d pos = p.getPos();
                Vec3d offset = new Vec3d(
                        random.nextDouble() * (fireworkOffset * 2) - fireworkOffset,
                        1,
                        random.nextDouble() * (fireworkOffset * 2) - fireworkOffset
                );
                firework.refreshPositionAfterTeleport(pos.add(offset));
                p.getWorld().spawnEntity(firework);
            }

            this.stopGame();
        }
        return true;
    }

    public void prepareStartGame() {
        this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.already.broadcast")
                .formatted(Formatting.GREEN), false);
        this.startTask = new LimitRepeatTask(
                () -> {
                    for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
                        PlayerUtil.sendTitle(player1, Text.literal(String.valueOf(this.startTask.getCount()))
                                .formatted(Formatting.GREEN));
                        player1.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                () -> this.startGame(false),
                0, 20,
                Variables.config.delay.gameStartDelaySeconds
        );
        TaskExecutor.INSTANCE.execute(this.startTask);
    }

    public void prepareResetWorlds(Collection<UUID> participants) {
        this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.start.broadcast",
                Variables.config.delay.serverCloseDelaySeconds)
                .formatted(Formatting.GREEN), false);
        this.stopTask = new LimitRepeatTask(
                () -> {
                    for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                        PlayerUtil.sendTitle(player, Text.literal(String.valueOf(this.stopTask.getCount()))
                                .formatted(Formatting.GREEN));
                        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7f, 1.0f);
                    }
                },
                () -> this.stopServer(Text.translatable("battlegrounds.game.server.stop")
                        .formatted(Formatting.GREEN)),
                0, 20, Variables.config.delay.serverCloseDelaySeconds
        );
        TaskExecutor.INSTANCE.execute(this.stopTask);
        FileUtil.writeString(
                Constants.SAVE_PATH_TMP_FILE,
                this.server.getSavePath(WorldSavePath.ROOT).toString()
        );
        HashMap<UUID, GameProgress.PlayerPermission> playerList = new HashMap<>();
        for (UUID uuid : participants) {
            GameProgress.PlayerPermission permission = new GameProgress.PlayerPermission();
            permission.gameMode = GameMode.ADVENTURE;
            permission.inGame = true;
            playerList.put(
                    uuid,
                    permission
            );
        }
        Variables.progress.players = new HashMap<>(playerList);
        this.setInitialProgress();
        Variables.progress.gameStage = GameProgress.GameStage.RESET_WORLD;
    }

    public void tryResetWorlds() {
        if (Files.isRegularFile(Constants.SAVE_PATH_TMP_FILE)) {
            this.resetWorlds();
            FileUtil.delete(Constants.SAVE_PATH_TMP_FILE, true);
            Constants.LOGGER.info("已重置地图");
        }
    }

    private void resetWorlds() {
        Path savePath = Path.of(FileUtil.readString(Constants.SAVE_PATH_TMP_FILE).trim());
        FileUtil.delete(savePath, false, "bg_progress.json");
    }

    public void setInitialProgress() {
        Variables.progress.pvpMode = GameProgress.PVPMode.PEACEFUL;
        Variables.progress.currentLap = 0;
        Variables.progress.resizeLapTimer = Variables.config.border.time.resizeDelayTicks;
    }

    public void setKeepInventory(boolean value) {
        this.server.getGameRules().get(GameRules.KEEP_INVENTORY).set(value, this.server);
    }

    public int getInGamePlayersNum() {
        int num = 0;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (Variables.progress.isInGame(PlayerUtil.getUUID(player))) num++;
        }
        return num;
    }

    public ServerPlayerEntity getLastInGamePlayer() {
        ServerPlayerEntity result = null;
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            if (Variables.progress.isInGame(PlayerUtil.getUUID(player))) {
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
        List<ServerPlayerEntity> players = Variables.progress.players.keySet().stream().map((uuid) ->
                this.server.getPlayerManager().getPlayer(uuid)).toList();
        this.teamHelper.assignPlayers(players);
    }

    public void startGame(boolean assignTeam) {
        World world = this.server.getOverworld();
        this.worldHelper = new WorldHelper(world);
        BlockPos pos = RandomUtil.getSecureLocation(world);
        this.worldHelper.setBorderCenter(pos.getX(), pos.getZ());
        this.worldHelper.setBorderSize(Variables.config.border.initialSize);
        this.server.getPlayerManager().setWhitelistEnabled(true);
        if (assignTeam) {
            this.teamHelper.setMaxPlayers(1);
            this.assignTeams();
        }

        for (UUID uuid : Variables.progress.players.keySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
            if (player != null && Variables.progress.isInGame(uuid)) {
                EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attribute != null;
                EffectUtil.addAttribute(attribute, healthModifierId, Variables.config.attributes.genericAdditionHealth, EntityAttributeModifier.Operation.ADDITION);
                player.setHealth(player.getMaxHealth());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 15 * 20, 3));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 15 * 20, 3));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 4));
                GameProgress.PlayerPermission permission = Variables.progress.players.get(uuid);
                permission.hasEffects = true;
                permission.naturalRegen = false;

                player.getInventory().clear();
                PlayerUtil.setGameMode(player, GameMode.SURVIVAL);
            }
        }
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            PlayerUtil.randomTeleport(this.server.getOverworld(), player);
        }
        Variables.progress.pvpMode = GameProgress.PVPMode.NO_PVP;
        Variables.progress.gameStage = GameProgress.GameStage.DEVELOP;
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
            this.bossBar = manager.add(BAR_ID, Text.translatable("battlegrounds.border.bar",
                            this.reduceTask.getDelay()/ 20)
                    .formatted(Formatting.GREEN));
        }
        this.barUpdateTask = new RepeatTask(() -> {
            if (this.bossBar == null) {
                throw new TaskCancelledException();
            }
            this.bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
            this.bossBar.setMaxValue((int) (Variables.config.border.time.resizeDelayTicks + Variables.config.border.time.resizeSpendTicks));
            this.bossBar.setValue((int) this.reduceTask.getDelay());
            this.bossBar.setName(Text.translatable("battlegrounds.border.bar",
                            this.reduceTask.getDelay()/ 20)
                    .formatted(Formatting.GREEN));
        }, this.reduceTask.getDelay() % 20, 20);
        TaskExecutor.INSTANCE.execute(barUpdateTask);
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
        this.reduceTask = new RepeatTask(() -> {
            // 启用 PVP
            if (Variables.progress.currentLap + 1
                    == Variables.config.border.borderOrdinal.pvpEnabledBorderOrdinal) {
                Variables.progress.pvpMode = GameProgress.PVPMode.PVP_MODE;
                for (UUID uuid : Variables.progress.players.keySet()) {
                    if (Variables.progress.isInGame(uuid)) {
                        Variables.progress.players.get(uuid).hasEffects = false;
                    }
                }

                this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.pvp.enable.broadcast")
                        .formatted(Formatting.GOLD), false);
                this.worldHelper.setBorderSize(worldHelper.getBorderSize() - Variables.config.border.resizeBlocks,
                        Variables.config.border.time.resizeSpendTicks * 50);
            }
            // 最终圈
            if (Variables.progress.currentLap + 1
                    == Variables.config.border.borderOrdinal.finalBorderOrdinal) {
                this.worldHelper.setBorderSize(worldHelper.getBorderSize() - Variables.config.border.resizeBlocks,
                        Variables.config.border.time.resizeSpendTicks * 50);
            }
            // 死亡竞赛-提示
            if (Variables.progress.currentLap + 1
                    == Variables.config.border.borderOrdinal.deathmatchBeginBorderOrdinal) {
                this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.deathmatch.already.broadcast",
                                (Variables.config.border.time.resizeDelayTicks + Variables.config.border.time.resizeSpendTicks) / 1200)
                        .formatted(Formatting.GOLD), false);
            }
            // 死亡竞赛-初始圈
            if (Variables.progress.currentLap
                    == Variables.config.border.borderOrdinal.deathmatchBeginBorderOrdinal) {
                Variables.progress.gameStage = GameProgress.GameStage.DEATHMATCH;
                this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.deathmatch.start.broadcast")
                        .formatted(Formatting.GOLD), false);
                this.worldHelper.setBorderSize(Variables.config.border.deathmatch.initialSize);
                for (UUID uuid : Variables.progress.players.keySet()) {
                    if (Variables.progress.isInGame(uuid)) {
                        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
                        if (player != null) {
                            player.clearStatusEffects();
                            player.setHealth(player.getMaxHealth());
                        }
                    }
                }
                for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                    PlayerUtil.randomTeleport(this.server.getOverworld(), player);
                }
                this.worldHelper.setBorderSize(Variables.config.border.deathmatch.finalSize,
                        Variables.config.border.deathmatch.resizeSpendTicks * 50);
            }
            // 死亡竞赛-最终圈
            if (Variables.progress.currentLap
                    >= Variables.config.border.totalNum) {
                this.removeBossBar();
                throw new TaskCancelledException();
            }
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 1);
            }
            this.server.getPlayerManager().broadcast(Text.translatable("battlegrounds.game.border.reduce.broadcast")
                    .formatted(Formatting.GOLD), false);
            Variables.progress.currentLap++;
        }, Variables.progress.resizeLapTimer,
                () -> Variables.config.border.time.resizeDelayTicks + Variables.config.border.time.resizeSpendTicks);
        TaskExecutor.INSTANCE.execute(this.reduceTask);
    }

    public void stopGame() {
        for (ServerPlayerEntity player : Variables.server.getPlayerManager().getPlayerList()) {
            PlayerUtil.setGameMode(player, GameMode.ADVENTURE);
            player.getInventory().clear();
        }

        Variables.progress.gameStage = GameProgress.GameStage.IDLE;
        this.setInitialProgress();
        this.removeBossBar();
        this.reduceTask.cancel();
        this.server.getPlayerManager().setWhitelistEnabled(false);
        this.worldHelper.setBorderSize(Variables.config.border.initialSize);
        for (UUID uuid : Variables.progress.players.keySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attribute != null;
                EffectUtil.removeAttribute(attribute, this.healthModifierId);
            }
        }
        ConfigUtil.saveConfig(Variables.server.getSavePath(WorldSavePath.ROOT), "bg_progress", new GameProgress());
    }
}
