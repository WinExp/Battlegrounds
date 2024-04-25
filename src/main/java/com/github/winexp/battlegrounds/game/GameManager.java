package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.network.packet.s2c.play.config.ModGameConfigS2CPacket;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.task.LimitRepeatTask;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.task.ScheduledTask;
import com.github.winexp.battlegrounds.task.ServerTaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.EntityUtil;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import com.github.winexp.battlegrounds.util.time.Duration;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CancellationException;

public class GameManager extends PersistentState implements GameListener {
    public final MinecraftServer server;
    private final WorldHelper worldHelper;
    private final Map<UUID, PlayerPermission> playerPermissions = new HashMap<>();
    private final List<GameListener> listeners = new ArrayList<>(List.of(this));
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
    private final static Map<StatusEffect, Integer> ENRICH_EFFECTS = Map.of(
            StatusEffects.FIRE_RESISTANCE, 0,
            StatusEffects.RESISTANCE, 1,
            StatusEffects.SPEED, 1
    );
    private final static int ENRICH_EFFECTS_DURATION = 24 * 20;
    private final static Map<StatusEffect, Integer> RESIDENT_EFFECTS = Map.of(
            StatusEffects.HASTE, 1
    );
    private final static int RESIDENT_EFFECTS_DURATION = 24 * 20;
    private final static Identifier RESIZE_BOSS_BAR_ID = new Identifier("battlegrounds", "resize_boss_bar");
    private final static Identifier HEALTH_MODIFIER_ID = new Identifier("battlegrounds", "game/main");
    private final static double HEALTH_MODIFIER_ADD_VALUE = 20;
    private final static List<GameListener> globalListeners = new ArrayList<>();

    public GameManager(MinecraftServer server) {
        this.server = server;
        this.worldHelper = new WorldHelper(server.getOverworld());
        this.listeners.addAll(globalListeners);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::giveEffects);
        ModServerPlayerEvents.AFTER_PLAYER_JOINED.register(this::onPlayerJoin);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingEntityDeath);
        ServerLivingEntityEvents.ALLOW_DEATH.register(this::allowLivingEntityDeath);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
    }

    private void onServerStopping(MinecraftServer server) {
        this.disableInfoBossBar();
    }

    @Override
    public void onBorderResizing(GameManager manager) {
        manager.server.getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.border.reduce.broadcast")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withUnderline(true)),
                false
        );
    }

    @Override
    public void onBorderResized(GameManager manager) {
        if (manager.resizeCount >= manager.currentStage.resizeCount()) {
            int prevStageIdx = manager.gameProperties.stages().indexOf(currentStage);
            if (prevStageIdx == manager.gameProperties.stages().size() - 1) {
                manager.disableBorderResizing();
                manager.listeners.forEach(listener ->
                        listener.onStageTriggered(manager, new Identifier("battlegrounds", "final")));
            } else {
                manager.currentStage = manager.gameProperties.stages().get(prevStageIdx + 1);
                int oldSize = manager.worldHelper.getBorderSize();
                manager.worldHelper.setBorderSize(manager.currentStage.initialSize());
                if (oldSize - manager.currentStage.initialSize() > manager.worldHelper.getBorder().getSafeZone()) {
                    PlayerUtil.randomTpAllPlayers(manager.server, manager.server.getOverworld());
                }
                manager.listeners.forEach(listener ->
                        listener.onStageTriggered(manager, manager.currentStage.trigger()));
            }
            manager.resizeCount = 0;
        }
    }

    @Override
    public void onPlayerWin(GameManager manager, ServerPlayerEntity player) {
        Random random = player.getRandom();
        manager.server.getPlayerManager().broadcast(
                Text.translatable("game.battlegrounds.won.broadcast", player.getDisplayName())
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withBold(true).withUnderline(true)),
                false
        );
        int fireworkAmount = random.nextBetween(2, 4);
        GameUtil.spawnWinnerFireworks(player, fireworkAmount, 4);
        manager.stopGame();
    }

    @Override
    public void onStageTriggered(GameManager manager, @Nullable Identifier triggerId) {
        if (triggerId == null) return;
        manager.syncData();
        if (triggerId.equals(new Identifier("battlegrounds", "enable_pvp"))) {
            manager.pvpMode = PVPMode.PVP_MODE;
            for (ServerPlayerEntity player : manager.server.getPlayerManager().getPlayerList()) {
                UUID uuid = PlayerUtil.getAuthUUID(player);
                PlayerPermission permission = manager.getPlayerPermission(uuid, new PlayerPermission());
                if (permission.inGame) {
                    permission.hasEnrichEffects = false;
                }
            }
            manager.server.getPlayerManager().broadcast(
                    Text.translatable("game.battlegrounds.pvp.enable.broadcast")
                            .formatted(Formatting.GOLD)
                            .styled(style -> style.withUnderline(true)),
                    false
            );
        } else if (triggerId.equals(new Identifier("battlegrounds", "deathmatch"))) {
            manager.server.getPlayerManager().broadcast(
                    Text.translatable("game.battlegrounds.deathmatch.start.broadcast")
                            .formatted(Formatting.GOLD)
                            .styled(style -> style.withBold(true).withUnderline(true)),
                    false
            );
        } else if (triggerId.equals(new Identifier("battlegrounds", "final"))) {
            manager.gameStage = GameStage.FINAL;
            manager.enableTimeoutTimer(manager.gameProperties.timeout().toTicks());
        }
    }

    @Override
    public void onGameTie(GameManager manager) {
        manager.server.getPlayerManager().broadcast(Text.translatable("game.battlegrounds.tie")
                .formatted(Formatting.GOLD)
                .styled(style -> style
                        .withBold(true)
                        .withUnderline(true)), false);
        manager.stopGame();
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
                                this.getTotalInGamePlayerCount()
                        ).formatted(Formatting.GREEN),
                        false
                );
                if (this.checkAllPlayersJoined() && this.startTask.isCancelled()) {
                    this.startTask = new LimitRepeatTask(Duration.ZERO, Duration.withSeconds(1), 10) {
                        @Override
                        public void onTriggered() throws CancellationException {
                            PlayerUtil.broadcastTitle(server, Text.literal(String.valueOf(this.getCount()))
                                    .formatted(Formatting.GREEN));
                            PlayerUtil.broadcastSound(server,
                                    SoundCategory.NEUTRAL,
                                    SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(),
                                    0.7F, 1.0F);
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
                    ServerTaskScheduler.INSTANCE.schedule(this.startTask);
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

    private void onLivingEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity && this.gameStage.isGaming() && this.currentStage != null) {
            if (this.getJoinedInGamePlayerCount() == 1) {
                ServerPlayerEntity winner = this.getLastInGamePlayer();
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
                permission.inGame = false;
                PlayerUtil.setGameModeToMap(player, GameMode.SPECTATOR);
            } else if (permission.respawnChance > 0) {
                player.setHealth(player.getMaxHealth());
                player.playSound(SoundEvents.ITEM_TOTEM_USE, SoundCategory.NEUTRAL, 0.7F, 1.0F);
                player.networkHandler.sendPacket(new ParticleS2CPacket(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        false, 0, 0, 0,
                        0, 0, 0, 0, 1));
                permission.respawnChance--;
                return false;
            }
            if (this.getJoinedInGamePlayerCount() == 1) {
                ServerPlayerEntity winner = this.getLastInGamePlayer();
                this.listeners.forEach(listener ->
                        listener.onPlayerWin(this, winner));
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
                    ENRICH_EFFECTS.forEach((effect, amplifier) ->
                            player.addStatusEffect(new StatusEffectInstance(effect, ENRICH_EFFECTS_DURATION, amplifier, false, false , true)));
                }
                RESIDENT_EFFECTS.forEach((effect, amplifier) ->
                        player.addStatusEffect(new StatusEffectInstance(effect, RESIDENT_EFFECTS_DURATION, amplifier, false, false , true)));
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
        return gameStage;
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
        if (globalListeners.contains(listener)) throw new IllegalStateException();
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

    public void setGameProperties(@NotNull GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    public int getJoinedInGamePlayerCount() {
        int count = 0;
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            if (this.server.getPlayerManager().getPlayer(entry.getKey()) != null && entry.getValue().inGame) {
                count++;
            }
        }
        return count;
    }

    public int getTotalInGamePlayerCount() {
        int count = 0;
        for (PlayerPermission permission : this.playerPermissions.values()) {
            if (permission.inGame) {
                count++;
            }
        }
        return count;
    }

    public ServerPlayerEntity getLastInGamePlayer() {
        ServerPlayerEntity result = null;
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(entry.getKey());
            if (player != null && entry.getValue().inGame) {
                result = player;
            }
        }
        return result;
    }

    public boolean isParticipant(UUID uuid) {
        for (Map.Entry<UUID, PlayerPermission> entry : this.playerPermissions.entrySet()) {
            if (entry.getKey().equals(uuid) && entry.getValue().inGame) return true;
        }
        return false;
    }

    public boolean checkAllPlayersJoined() {
        return this.getJoinedInGamePlayerCount() == this.getTotalInGamePlayerCount();
    }

    public void enableInfoBossBar() {
        this.ensureIsGaming();
        this.ensureBorderResizeEnabled();
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
        ServerTaskScheduler.INSTANCE.schedule(this.updateBossBarTask);
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
        if (ServerTaskScheduler.INSTANCE.isRunning(this.timeoutTask)) {
            int timeoutTimeTicks = this.gameProperties.timeout().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.timeout",
                                    this.timeoutTask.getDelayTicks() / 20
                            )
                            .formatted(Formatting.RED));
            this.resizeBossBar.setMaxValue(timeoutTimeTicks);
            this.resizeBossBar.setValue(this.timeoutTask.getDelayTicks());
        } else if (this.borderStage == GameBorderStage.WAITING) {
            int delayTimeTicks = this.currentStage.resizeTimeInfo().delayTime().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.waiting",
                            this.resizeBorderTask.getDelayTicks() / 20
                    )
                    .formatted(Formatting.GREEN));
            this.resizeBossBar.setMaxValue(delayTimeTicks);
            this.resizeBossBar.setValue(this.resizeBorderTask.getDelayTicks());
        } else if (this.borderStage == GameBorderStage.RESIZING) {
            int spendTimeTicks = this.currentStage.resizeTimeInfo().spendTime().toTicks();
            this.resizeBossBar.setName(
                    Text.translatable(
                                    "game.battlegrounds.info_bossbar.resizing",
                                    this.borderResizingTask.getDelayTicks() / 20
                            )
                            .formatted(Formatting.GOLD));
            this.resizeBossBar.setMaxValue(spendTimeTicks);
            this.resizeBossBar.setValue(this.borderResizingTask.getDelayTicks());
        }
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
                        .formatted(Formatting.GREEN));
                PlayerUtil.broadcastSound(GameManager.this.server,
                        SoundCategory.NEUTRAL,
                        SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(),
                        0.7F, 1.0F);
            }

            @Override
            public void onCompleted() throws CancellationException {
                GameManager.this.stopServerToDeleteWorld(participants);
            }
        };
        ServerTaskScheduler.INSTANCE.schedule(stopTask);
    }

    public void stopServerToDeleteWorld(Collection<UUID> participants) {
        this.ensureGamePropertiesNotNull();
        this.stopGame();
        for (UUID uuid : participants) {
            PlayerPermission permission = new PlayerPermission();
            permission.inGame = true;
            this.setPlayerPermission(uuid, permission);
        }
        this.gameStage = GameStage.WAITING_PLAYER;
        GameUtil.createDeleteWorldTmpFile(this.server.getSavePath(WorldSavePath.ROOT));
        PlayerUtil.kickAllPlayers(this.server, Text.translatable("game.battlegrounds.server.stop")
                .formatted(Formatting.GREEN));

        this.server.stop(false);
    }

    private void syncData() {
        PlayerUtil.broadcastPacket(this.server, new ModGameConfigS2CPacket(this.currentStage != null
                ? this.currentStage.gameConfig() : Constants.DEFAULT_MOD_GAME_CONFIG));
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
            if (permission.inGame) {
                permission.hasEnrichEffects = true;
                PlayerUtil.changeGameMode(player, GameMode.SURVIVAL);
                EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attributeInstance != null;
                EntityUtil.addAttributeModifier(attributeInstance, HEALTH_MODIFIER_ID, HEALTH_MODIFIER_ADD_VALUE, EntityAttributeModifier.Operation.ADDITION);
                player.heal((float) HEALTH_MODIFIER_ADD_VALUE);
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
        this.syncData();
        this.listeners.forEach(listener ->
                listener.onStageTriggered(this, this.currentStage.trigger()));
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
        this.syncData();
        this.listeners.forEach(listener ->
                listener.onStageTriggered(this, null));
    }

    public void resumeGame(int resizeTimer, int resizingTimer, int timeoutTimer) {
        this.ensureIsGaming();
        this.ensureGamePropertiesNotNull();
        if (resizeTimer > 0) {
            this.enableBorderResizing(resizeTimer);
        }
        if (resizingTimer > 0) {
            this.startBorderResizingTimer(resizingTimer);
        }
        if (timeoutTimer > 0) {
            this.enableTimeoutTimer(timeoutTimer);
        }
        if (resizeTimer >= 0 || resizingTimer > 0 || timeoutTimer > 0) {
            this.enableInfoBossBar();
        }
        this.syncData();
    }

    public void enableTimeoutTimer(int ticksLeft) {
        this.timeoutTask = new ScheduledTask(Duration.withTicks(ticksLeft)) {
            @Override
            public void run() throws CancellationException {
                GameManager.this.listeners.forEach(listener ->
                        listener.onGameTie(GameManager.this));
            }
        };
        ServerTaskScheduler.INSTANCE.schedule(this.timeoutTask);
    }

    public void disableTimeoutTimer() {
        this.timeoutTask.cancel();
    }

    public void startBorderResizingTimer(int delayTicks) {
        this.borderResizingTask = new ScheduledTask(Duration.withTicks(delayTicks)) {
            @Override
            public void run() throws CancellationException {
                GameManager.this.listeners.forEach(listener -> listener.onBorderResized(GameManager.this));
                GameManager.this.borderStage = GameBorderStage.WAITING;
            }
        };
        ServerTaskScheduler.INSTANCE.schedule(this.borderResizingTask);
    }

    public void stopBorderResizingTimer() {
        this.borderResizingTask.cancel();
    }

    public void enableBorderResizing(int delayTicks) {
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
        ServerTaskScheduler.INSTANCE.schedule(this.resizeBorderTask);
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
        this.listeners.forEach(listener -> listener.onBorderResizing(this));
        this.startBorderResizingTimer(spendTime.toTicks());
    }

    public static PersistentState.Type<GameManager> getPersistentStateType(MinecraftServer server) {
        return new Type<>(() -> new GameManager(server),
                nbt -> createFromNbt(server, nbt), null);
    }

    public static GameManager getManager(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        GameManager gameManager = manager.getOrCreate(getPersistentStateType(server), PERSISTENT_STATE_ID);
        gameManager.markDirty();
        return gameManager;
    }

    @Override
    public void save(File file) {
        super.save(file);
        this.markDirty();
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
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("resize_count", this.resizeCount);
        nbt.put("pvp_mode", Util.getResult(PVPMode.CODEC.encodeStart(NbtOps.INSTANCE, this.pvpMode), IllegalStateException::new));
        nbt.put("game_stage", Util.getResult(GameStage.CODEC.encodeStart(NbtOps.INSTANCE, this.gameStage), IllegalStateException::new));
        nbt.put("border_stage", Util.getResult(GameBorderStage.CODEC.encodeStart(NbtOps.INSTANCE, this.borderStage), IllegalStateException::new));
        NbtList playerPermissionsList = new NbtList();
        this.playerPermissions.forEach((uuid, permission) -> {
            NbtCompound permissionNbt = permission.toNbt();
            permissionNbt.putUuid("uuid", uuid);
            playerPermissionsList.add(permissionNbt);
        });
        nbt.put("player_permissions", playerPermissionsList);
        if (this.gameProperties != null) {
            nbt.put("game_properties", Util.getResult(GameProperties.CODEC.encodeStart(NbtOps.INSTANCE, this.gameProperties), IllegalStateException::new));
        }
        if (this.currentStage != null) {
            nbt.put("current_stage", Util.getResult(GameProperties.StageInfo.CODEC.encodeStart(NbtOps.INSTANCE, this.currentStage), IllegalStateException::new));
        }
        if (this.gameStage.isGaming()) {
            nbt.putInt("resize_timer", this.resizeBorderTask.getDelayTicks());
            nbt.putInt("resizing_timer", this.borderResizingTask.getDelayTicks());
            if (ServerTaskScheduler.INSTANCE.isRunning(this.timeoutTask)) {
                nbt.putInt("timeout_timer", this.timeoutTask.getDelayTicks());
            }
        }
        return nbt;
    }
}
