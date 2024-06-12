package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.network.payload.s2c.play.config.ModGameConfigPayloadS2C;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import com.github.winexp.battlegrounds.util.task.LimitRepeatTask;
import com.github.winexp.battlegrounds.util.task.RepeatTask;
import com.github.winexp.battlegrounds.util.task.ScheduledTask;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.EntityUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.time.Duration;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.CancellationException;

public class GameManager extends PersistentState {
    private final MinecraftServer server;
    private final WorldHelper worldHelper;
    private final Map<UUID, PlayerPermission> playerPermissions = new HashMap<>();
    private final List<GameListener> listeners = new ArrayList<>();
    private PVPMode pvpMode = PVPMode.PEACEFUL;
    private GameStage gameStage = GameStage.IDLE;
    private GameBorderStage borderStage = GameBorderStage.WAITING;
    private GameProperties gameProperties;
    private GameProperties.StageInfo currentStage;
    private int resizeCount = 0;
    private LimitRepeatTask startTask = LimitRepeatTask.NONE_TASK;
    private ScheduledTask timeoutTask = ScheduledTask.NONE_TASK;
    private ScheduledTask borderResizingTask = ScheduledTask.NONE_TASK;
    private RepeatTask resizeBorderTask = RepeatTask.NONE_TASK;
    private RepeatTask updateBossBarTask = RepeatTask.NONE_TASK;
    private CommandBossBar resizeBossBar;
    public final static String PERSISTENT_STATE_ID = "game_info";
    private final static List<StatusEffectInstance> ENRICH_EFFECTS = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 24 * 20, 0, false, false, true),
            new StatusEffectInstance(StatusEffects.RESISTANCE, 24 * 20, 1, false, false, true),
            new StatusEffectInstance(StatusEffects.SPEED, 24 * 20, 1, false, false, true)
    );
    private final static List<StatusEffectInstance> RESIDENT_EFFECTS = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.HASTE, 24 * 20, 1, false, false, true)
    );
    private final static Identifier RESIZE_BOSS_BAR_ID = new Identifier("battlegrounds", "resize_boss_bar");
    private final static Identifier HEALTH_MODIFIER_ID = new Identifier("battlegrounds", "game/main");
    private final static double HEALTH_MODIFIER_ADD_VALUE = 20;
    private final static List<GameListener> globalListeners = new ArrayList<>();

    public GameManager(MinecraftServer server) {
        this.server = server;
        this.worldHelper = new WorldHelper(server.getOverworld());
        for (GameListener listener : globalListeners) {
            this.addListener(listener);
        }
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::giveEffects);
        ModServerPlayerEvents.AFTER_PLAYER_JOINED.register(this::onPlayerJoin);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingEntityDeath);
        ServerLivingEntityEvents.ALLOW_DEATH.register(this::allowLivingEntityDeath);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    private void onServerStopping(MinecraftServer server) {
        this.disableInfoBossBar();
    }

    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (this.resizeBossBar != null) {
            this.resizeBossBar.addPlayer(player);
        }
        if (this.gameStage == GameStage.WAITING_PLAYER) {
            if (this.isParticipant(uuid)) {
                PlayerUtil.changeGameMode(player, GameMode.ADVENTURE);
                server.getPlayerManager().broadcast(
                        Text.translatable("game.battlegrounds.join.broadcast",
                                player.getDisplayName(),
                                this.getJoinedInGamePlayerCount(),
                                this.getTotalAlivePlayerCount()
                        ).formatted(Formatting.GREEN),
                        false
                );
                if (this.checkAllPlayersJoined() && this.startTask.isCancelled()) {
                    this.startTask = new LimitRepeatTask(Duration.ZERO, Duration.withSeconds(1), 10) {
                        @Override
                        public void onTriggered() throws CancellationException {
                            PlayerUtil.broadcastTitle(server, Text.literal(String.valueOf(this.getCount()))
                                    .formatted(Formatting.GREEN), Text.empty());
                            PlayerUtil.broadcastSound(server, SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7F, 1.0F);
                            if (!GameManager.this.checkAllPlayersJoined()) {
                                this.cancel();
                                throw new CancellationException();
                            }
                        }

                        @Override
                        public void onCompleted() throws CancellationException {
                            if (!GameManager.this.checkAllPlayersJoined()) {
                                this.cancel();
                                throw new CancellationException();
                            }
                            GameManager.this.startGame();
                        }
                    };
                    TaskScheduler.INSTANCE.schedule(this.startTask);
                }
            } else {
                PlayerUtil.changeGameMode(player, GameMode.SPECTATOR);
                server.getPlayerManager().broadcast(
                        Text.translatable("game.battlegrounds.join.spectator.broadcast",
                                player.getDisplayName()
                        )
                                .formatted(Formatting.GRAY),
                        false
                );
            }
        }
    }

    private void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        PlayerUtil.changeGameModeWithMap(newPlayer);
    }

    private void onPlayerWin(ServerPlayerEntity winner) {
        Random random = winner.getRandom();
        this.server.getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.won.broadcast", winner.getDisplayName())
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withBold(true).withUnderline(true)),
                false
        );
        int fireworkAmount = random.nextBetween(2, 4);
        GameUtil.spawnWinnerFireworks(winner, fireworkAmount, 4);
        this.stopGame();
    }

    private void onLivingEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity && this.gameStage.isGaming() && this.currentStage != null) {
            if (this.getJoinedInGamePlayerCount() == 1) {
                ServerPlayerEntity winner = this.getLastAlivePlayer();
                this.onPlayerWin(winner);
                this.listeners.forEach(listener ->
                        listener.onPlayerWin(this, winner));
            }
        }
    }

    private boolean allowLivingEntityDeath(LivingEntity entity, DamageSource damageSource, float amount) {
        if (entity instanceof ServerPlayerEntity player && this.gameStage.isGaming() && this.currentStage != null) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
            if (!this.currentStage.allowRespawn()) {
                permission.isDead = true;
                PlayerUtil.setGameModeToMap(player, GameMode.SPECTATOR);
            } else if (permission.respawnChance > 0) {
                player.setHealth(player.getMaxHealth());
                player.playSound(SoundEvents.ITEM_TOTEM_USE, 0.7F, 1.0F);
                player.networkHandler.sendPacket(new ParticleS2CPacket(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        false, 0, 0, 0,
                        0, 0, 0, 0, 1));
                permission.respawnChance--;
                return false;
            }
        }
        return true;
    }

    private boolean allowLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            return this.pvpMode.isAllowDamage(source, player);
        } else return true;
    }

    private void giveEffects(MinecraftServer server) {
        this.playerPermissions.forEach((uuid, permission) -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                if (permission.hasEnrichEffects) {
                    for (StatusEffectInstance effectInstance : ENRICH_EFFECTS) {
                        player.addStatusEffect(new StatusEffectInstance(effectInstance));
                    }
                }
                for (StatusEffectInstance effectInstance : RESIDENT_EFFECTS) {
                    player.addStatusEffect(new StatusEffectInstance(effectInstance));
                }
            }
        });
    }

    public void ensureGamePropertiesNotNull() {
        if (this.gameProperties == null) throw new IllegalStateException();
    }

    public void ensureIsGaming() {
        if (!this.gameStage.isGaming()) throw new IllegalStateException();
    }

    public void ensureIsNotGaming() {
        if (this.gameStage.isGaming()) throw new IllegalStateException();
    }

    public void ensureBorderResizeEnabled() {
        if (this.resizeBorderTask.isCancelled()) throw new IllegalStateException();
    }

    public GameStage getGameStage() {
        return this.gameStage;
    }

    public void setGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    public PlayerPermission getPlayerPermission(UUID uuid) {
        return this.getPlayerPermission(uuid, null);
    }

    public PlayerPermission getPlayerPermission(UUID uuid, PlayerPermission defaultValue) {
        return this.playerPermissions.getOrDefault(uuid, defaultValue);
    }

    public void setPlayerPermission(UUID uuid, PlayerPermission permission) {
        this.playerPermissions.put(uuid, permission);
    }

    public void addListener(GameListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        this.listeners.remove(listener);
    }

    public static void addGlobalListener(GameListener listener) {
        globalListeners.add(listener);
    }

    public static void removeGlobalListener(GameListener listener) {
        globalListeners.remove(listener);
    }

    public PVPMode getPVPMode() {
        return this.pvpMode;
    }

    public void setPVPMode(PVPMode pvpMode) {
        this.pvpMode = pvpMode;
    }

    public GameProperties getGameProperties() {
        return this.gameProperties;
    }

    public GameBorderStage getBorderStage() {
        return this.borderStage;
    }

    public void setGameProperties(GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    public int getJoinedInGamePlayerCount() {
        int count = 0;
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            if (this.server.getPlayerManager().getPlayer(entry.getKey()) != null && !entry.getValue().isDead) {
                count++;
            }
        }
        return count;
    }

    public int getTotalAlivePlayerCount() {
        int count = 0;
        for (PlayerPermission permission : this.playerPermissions.values()) {
            if (!permission.isDead) {
                count++;
            }
        }
        return count;
    }

    public ServerPlayerEntity getLastAlivePlayer() {
        ServerPlayerEntity result = null;
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(entry.getKey());
            if (player != null && !entry.getValue().isDead) {
                result = player;
            }
        }
        return result;
    }

    public boolean isParticipant(ServerPlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        return this.isParticipant(uuid);
    }

    public boolean isParticipant(UUID uuid) {
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            if (entry.getKey().equals(uuid) && !entry.getValue().isDead) return true;
        }
        return false;
    }

    public boolean checkAllPlayersJoined() {
        return this.getJoinedInGamePlayerCount() == this.getTotalAlivePlayerCount();
    }

    public void enableInfoBossBar() {
        this.ensureIsGaming();
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bossBar = manager.get(RESIZE_BOSS_BAR_ID);
        if (bossBar == null) {
            bossBar = manager.add(RESIZE_BOSS_BAR_ID, Text.empty());
        }
        bossBar.clearPlayers();
        bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
        this.resizeBossBar = bossBar;
        this.updateInfoBossBar();
        this.updateBossBarTask = new RepeatTask(Duration.withTicks(this.resizeBorderTask.getDelayTicks() % 20), Duration.withSeconds(1)) {
            @Override
            public void onTriggered() throws CancellationException {
                GameManager.this.updateInfoBossBar();
            }
        };
        TaskScheduler.INSTANCE.schedule(this.updateBossBarTask);
    }

    public void disableInfoBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        if (this.resizeBossBar != manager.get(RESIZE_BOSS_BAR_ID)) {
            if (this.resizeBossBar != null) {
                this.resizeBossBar.clearPlayers();
                manager.remove(this.resizeBossBar);
            }
            this.resizeBossBar = manager.get(RESIZE_BOSS_BAR_ID);
        }
        if (this.resizeBossBar == null) return;
        this.resizeBossBar.clearPlayers();
        this.updateBossBarTask.cancel();
        manager.remove(this.resizeBossBar);
    }

    private void updateInfoBossBar() {
        if (!this.gameStage.isGaming()) return;
        if (TaskScheduler.INSTANCE.isRunning(this.timeoutTask)) {
            int timeoutTimeTicks = this.gameProperties.timeout().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.timeout",
                                    this.timeoutTask.getDelayTicks() / 20
                            )
                            .formatted(Formatting.RED));
            this.resizeBossBar.setMaxValue(timeoutTimeTicks);
            this.resizeBossBar.setValue(this.timeoutTask.getDelayTicks());
        } else if (this.borderStage == GameBorderStage.WAITING && TaskScheduler.INSTANCE.isRunning(this.resizeBorderTask)) {
            int delayTimeTicks = this.currentStage.resizeTimeInfo().delayTime().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.waiting",
                            this.resizeBorderTask.getDelayTicks() / 20
                    )
                    .formatted(Formatting.GREEN));
            this.resizeBossBar.setMaxValue(delayTimeTicks);
            this.resizeBossBar.setValue(this.resizeBorderTask.getDelayTicks());
        } else if (this.borderStage == GameBorderStage.RESIZING && TaskScheduler.INSTANCE.isRunning(this.borderResizingTask)) {
            int spendTimeTicks = this.currentStage.resizeTimeInfo().spendTime().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.resizing",
                                    this.borderResizingTask.getDelayTicks() / 20
                            )
                            .formatted(Formatting.GOLD));
            this.resizeBossBar.setMaxValue(spendTimeTicks);
            this.resizeBossBar.setValue(this.borderResizingTask.getDelayTicks());
        } else {
            this.updateBossBarTask.cancel();
        }
    }

    public void spawnPlayer(ServerPlayerEntity player, int respawnChance) {
        this.ensureIsGaming();
        UUID uuid = PlayerUtil.getAuthUUID(player);
        PlayerPermission playerPermission = this.getPlayerPermission(uuid, new PlayerPermission());
        playerPermission.gameMode = GameMode.SURVIVAL;
        playerPermission.isDead = false;
        playerPermission.respawnChance = respawnChance;
        this.setPlayerPermission(uuid, playerPermission);
        PlayerUtil.changeGameModeWithMap(player);
    }

    public void setIdleState() {
        this.gameStage = GameStage.IDLE;
        this.pvpMode = PVPMode.PEACEFUL;
        this.borderStage = GameBorderStage.WAITING;
        this.worldHelper.setDefaultBorder();
    }

    public void prepareToDeleteWorld(Collection<UUID> participants) {
        this.ensureGamePropertiesNotNull();
        this.server.getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.delete_world.broadcast", 10)
                        .formatted(Formatting.GREEN)
                        .styled(style -> style.withUnderline(true)),
                false
        );
        LimitRepeatTask stopTask = new LimitRepeatTask(Duration.ZERO, Duration.withSeconds(1), 10) {
            @Override
            public void onTriggered() throws CancellationException {
                PlayerUtil.broadcastTitle(GameManager.this.server, Text.literal(String.valueOf(this.getCount()))
                        .formatted(Formatting.GREEN), Text.empty());
                PlayerUtil.broadcastSound(GameManager.this.server, SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.NEUTRAL, 0.7F, 1.0F);
            }

            @Override
            public void onCompleted() throws CancellationException {
                GameManager.this.stopServerToDeleteWorld(participants);
            }
        };
        TaskScheduler.INSTANCE.schedule(stopTask);
    }

    public void stopServerToDeleteWorld(Collection<UUID> participants) {
        this.ensureGamePropertiesNotNull();
        this.stopGame();
        for (UUID uuid : participants) {
            PlayerPermission permission = new PlayerPermission();
            permission.isDead = false;
            this.setPlayerPermission(uuid, permission);
        }
        this.gameStage = GameStage.WAITING_PLAYER;
        GameUtil.createDeleteWorldTmpFile(this.server.getSavePath(WorldSavePath.ROOT));
        PlayerUtil.kickAllPlayers(this.server, Text.translatable("game.battlegrounds.server.stop")
                .formatted(Formatting.GREEN));

        this.server.stop(false);
    }

    private void syncData() {
        PlayerUtil.broadcastPacket(this.server, new ModGameConfigPayloadS2C(this.currentStage != null
                ? this.currentStage.gameConfig() : ModGameConfig.DEFAULT));
    }

    private void onStageTriggered(GameTrigger gameTrigger) {
        gameTrigger.apply(this);
    }

    public void startGame(){
        this.ensureIsNotGaming();
        this.ensureGamePropertiesNotNull();
        this.currentStage = this.gameProperties.stages().get(0);
        this.resizeCount = 0;
        World world = this.server.getOverworld();
        BlockPos borderCenter = RandomUtil.getSecureLocation(world);
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            player.getInventory().clear();
            UUID uuid = PlayerUtil.getAuthUUID(player);
            PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
            if (!permission.isDead) {
                permission.hasEnrichEffects = true;
                PlayerUtil.changeGameMode(player, GameMode.SURVIVAL);
                EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attributeInstance != null;
                EntityUtil.addAttributeModifier(attributeInstance, HEALTH_MODIFIER_ID, HEALTH_MODIFIER_ADD_VALUE, EntityAttributeModifier.Operation.ADD_VALUE);
                player.setHealth(player.getMaxHealth());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30 * 20, 3), player);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 30 * 20, 1), player);
            }
        }
        this.gameStage = GameStage.GAMING;
        this.pvpMode = PVPMode.NO_PVP;
        this.worldHelper.setBorderCenter(borderCenter.getX(), borderCenter.getZ());
        this.worldHelper.setBorderSize(this.currentStage.initialSize());
        PlayerUtil.randomTpAllPlayers(this.server, this.server.getOverworld());
        this.enableBorderResizing(this.currentStage.resizeTimeInfo().delayTime().toTicks());
        this.enableInfoBossBar();
        this.trigger(this.currentStage.triggers());
        this.syncData();
    }

    public void stopGame() {
        this.disableInfoBossBar();
        this.stopBorderResizingTimer();
        this.disableBorderResizing();
        this.disableTimeoutTimer();
        this.setIdleState();
        this.currentStage = null;
        this.resizeCount = 0;
        this.playerPermissions.clear();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            player.getInventory().clear();
            PlayerUtil.changeGameMode(player, GameMode.ADVENTURE);
            EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            assert attributeInstance != null;
            EntityUtil.removeAttributeModifier(attributeInstance, HEALTH_MODIFIER_ID);
        }
        this.trigger(GameTriggers.NONE);
        this.syncData();
    }

    public void resumeGame(int resizeTimer, int resizingTimer, int timeoutTimer) {
        this.ensureIsGaming();
        this.ensureGamePropertiesNotNull();
        this.enableBorderResizing(resizeTimer);
        this.startBorderResizingTimer(resizingTimer);
        this.enableTimeoutTimer(timeoutTimer);
        this.enableInfoBossBar();
        this.syncData();
    }

    public void trigger(List<GameTrigger> gameTriggers) {
        for (GameTrigger gameTrigger : gameTriggers) {
            this.trigger(gameTrigger);
        }
    }

    public void trigger(GameTrigger gameTrigger) {
        this.onStageTriggered(gameTrigger);
        this.listeners.forEach(listener ->
                listener.onTriggered(this, gameTrigger));
    }

    public void enableTimeoutTimer(int ticksLeft) {
        this.ensureIsGaming();
        if (ticksLeft < 0) return;
        this.timeoutTask = new ScheduledTask(Duration.withTicks(ticksLeft)) {
            @Override
            public void run() throws CancellationException {
                GameManager.this.server.getPlayerManager().broadcast(Text.translatable("game.battlegrounds.tie")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style
                                .withBold(true)
                                .withUnderline(true)), false);
                GameManager.this.stopGame();
                GameManager.this.listeners.forEach(listener ->
                        listener.onTimeout(GameManager.this));
            }
        };
        TaskScheduler.INSTANCE.schedule(this.timeoutTask);
    }

    public void disableTimeoutTimer() {
        this.timeoutTask.cancel();
    }

    public void startBorderResizingTimer(int delayTicks) {
        this.ensureIsGaming();
        if (delayTicks < 0) return;
        this.borderResizingTask = new ScheduledTask(Duration.withTicks(delayTicks)) {
            @Override
            public void run() throws CancellationException {
                if (GameManager.this.resizeCount >= GameManager.this.currentStage.resizeCount()) {
                    int prevStageIdx = GameManager.this.gameProperties.stages().indexOf(currentStage);
                    if (prevStageIdx == GameManager.this.gameProperties.stages().size() - 1) {
                        GameManager.this.disableBorderResizing();
                        GameManager.this.trigger(GameTriggers.FINAL_BEGIN);
                    } else {
                        GameManager.this.currentStage = GameManager.this.gameProperties.stages().get(prevStageIdx + 1);
                        int oldSize = GameManager.this.worldHelper.getBorderSize();
                        GameManager.this.worldHelper.setBorderSize(GameManager.this.currentStage.initialSize());
                        if (oldSize - GameManager.this.currentStage.initialSize() > GameManager.this.worldHelper.getBorder().getSafeZone()) {
                            PlayerUtil.randomTpAllPlayers(GameManager.this.server, GameManager.this.server.getOverworld());
                        }
                        GameManager.this.trigger(GameManager.this.currentStage.triggers());
                    }
                    GameManager.this.resizeCount = 0;
                }
                GameManager.this.listeners.forEach(listener -> listener.onBorderResized(GameManager.this));
                GameManager.this.borderStage = GameBorderStage.WAITING;
            }
        };
        TaskScheduler.INSTANCE.schedule(this.borderResizingTask);
    }

    public void stopBorderResizingTimer() {
        this.borderResizingTask.cancel();
    }

    public void enableBorderResizing(int delayTicks) {
        if (delayTicks < 0) return;
        this.ensureIsGaming();
        this.resizeBorderTask = new RepeatTask(Duration.withTicks(delayTicks), () -> {
            GameProperties.StageInfo.ResizeTimeInfo resizeTimeInfo = this.currentStage.resizeTimeInfo();
            return resizeTimeInfo.delayTime().add(resizeTimeInfo.spendTime());
        }) {
            @Override
            public void onTriggered() throws CancellationException {
                GameManager.this.resizeBorder();
            }
        };
        TaskScheduler.INSTANCE.schedule(this.resizeBorderTask);
    }

    public void disableBorderResizing() {
        this.resizeBorderTask.cancel();
        this.stopBorderResizingTimer();
    }

    private void resizeBorder() {
        this.ensureIsGaming();
        Duration spendTime = this.currentStage.resizeTimeInfo().spendTime();
        this.worldHelper.interpolateBorderSize(
                this.currentStage.initialSize() - (this.currentStage.resizeBlocks() * this.resizeCount),
                this.currentStage.initialSize() - (this.currentStage.resizeBlocks() * (this.resizeCount + 1)),
                spendTime.toMillis()
        );
        this.borderStage = GameBorderStage.RESIZING;
        this.resizeCount++;
        this.server.getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.border.reduce.broadcast")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withUnderline(true)),
                false
        );
        this.listeners.forEach(listener -> listener.onBorderResizing(this));
        this.startBorderResizingTimer(spendTime.toTicks());
    }

    public static PersistentState.Type<GameManager> getPersistentStateType(MinecraftServer server) {
        return new Type<>(() -> new GameManager(server),
                (nbt, wrapperLookup) -> createFromNbt(server, nbt), null);
    }

    public static GameManager getManager(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        GameManager gameManager = manager.getOrCreate(getPersistentStateType(server), PERSISTENT_STATE_ID);
        gameManager.markDirty();
        return gameManager;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static GameManager createFromNbt(MinecraftServer server, NbtCompound nbt) {
        GameManager manager = new GameManager(server);
        manager.resizeCount = nbt.getInt("resize_count");
        manager.pvpMode = PVPMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("pvp_mode"))
                .result().orElse(PVPMode.PEACEFUL);
        manager.gameStage = GameStage.CODEC.parse(NbtOps.INSTANCE, nbt.get("game_stage"))
                .result().orElse(GameStage.IDLE);
        manager.borderStage = GameBorderStage.CODEC.parse(NbtOps.INSTANCE, nbt.get("border_stage"))
                .result().orElse(GameBorderStage.WAITING);
        NbtList playerPermissionsList = (NbtList) nbt.get("player_permissions");
        if (playerPermissionsList == null) throw new IllegalStateException();
        for (NbtElement element : playerPermissionsList) {
            NbtCompound permissionNbt = (NbtCompound) element;
            PlayerPermission permission = PlayerPermission.createFromNbt(permissionNbt);
            UUID uuid = permissionNbt.getUuid("uuid");
            manager.setPlayerPermission(uuid, permission);
        }
        manager.gameProperties = GameProperties.CODEC.parse(NbtOps.INSTANCE, nbt.get("game_properties"))
                .result().orElse(null);
        if (nbt.contains("current_stage")) {
            manager.currentStage = GameProperties.StageInfo.CODEC.parse(NbtOps.INSTANCE, nbt.get("current_stage"))
                    .result().orElse(null);
        }
        if (manager.gameStage.isGaming()) {
            int resizeTimer = Codec.INT.parse(NbtOps.INSTANCE, nbt.get("resize_timer"))
                    .result().orElse(0);
            int resizingTimer = Codec.INT.parse(NbtOps.INSTANCE, nbt.get("resizing_timer"))
                    .result().orElse(0);
            int timeoutTimer = -1;
            if (nbt.contains("timeout_timer")) {
                timeoutTimer = Codec.INT.parse(NbtOps.INSTANCE, nbt.get("timeout_timer"))
                        .result().orElse(manager.gameProperties.timeout().toTicks());
            }
            manager.resumeGame(resizeTimer, resizingTimer, timeoutTimer);
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbt.putInt("resize_count", this.resizeCount);
        nbt.put("pvp_mode", PVPMode.CODEC.encodeStart(NbtOps.INSTANCE, this.pvpMode).getOrThrow());
        nbt.put("game_stage", GameStage.CODEC.encodeStart(NbtOps.INSTANCE, this.gameStage).getOrThrow());
        nbt.put("border_stage", GameBorderStage.CODEC.encodeStart(NbtOps.INSTANCE, this.borderStage).getOrThrow());
        NbtList playerPermissionsList = new NbtList();
        this.playerPermissions.forEach((uuid, permission) -> {
            NbtCompound permissionNbt = permission.toNbt();
            permissionNbt.putUuid("uuid", uuid);
            playerPermissionsList.add(permissionNbt);
        });
        nbt.put("player_permissions", playerPermissionsList);
        if (this.gameProperties != null) {
            nbt.put("game_properties", GameProperties.CODEC.encodeStart(NbtOps.INSTANCE, this.gameProperties).getOrThrow());
        }
        if (this.currentStage != null) {
            nbt.put("current_stage", GameProperties.StageInfo.CODEC.encodeStart(NbtOps.INSTANCE, this.currentStage).getOrThrow());
        }
        if (this.gameStage.isGaming()) {
            nbt.putInt("resize_timer", this.resizeBorderTask.getDelayTicks());
            nbt.putInt("resizing_timer", this.borderResizingTask.getDelayTicks());
            if (TaskScheduler.INSTANCE.isRunning(this.timeoutTask)) {
                nbt.putInt("timeout_timer", this.timeoutTask.getDelayTicks());
            }
        }
        return nbt;
    }
}
