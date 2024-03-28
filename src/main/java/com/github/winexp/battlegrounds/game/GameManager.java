package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.event.ServerGameEvents;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.task.LimitRepeatTask;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.task.ScheduledTask;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.EffectUtil;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GameManager extends PersistentState {
    public final MinecraftServer server;
    private final WorldHelper worldHelper;
    private final HashMap<UUID, PlayerPermission> playerPermissions = new HashMap<>();
    private PVPMode pvpMode = PVPMode.PEACEFUL;
    private GameStage gameStage = GameStage.IDLE;
    private GameProperties gameProperties;
    private GameProperties.StageInfo currentStage;
    private int resizeCount = 0;
    private LimitRepeatTask startTask = LimitRepeatTask.NONE_TASK;
    private RepeatTask resizeBorderTask = RepeatTask.NONE_TASK;
    private RepeatTask updateBossBarTask = RepeatTask.NONE_TASK;
    private CommandBossBar resizeBossBar;
    public final static String PERSISTENT_STATE_ID = "game_info";
    private final static Map<StatusEffect, Integer> ENRICH_EFFECTS = Map.of(
            StatusEffects.FIRE_RESISTANCE, 0,
            StatusEffects.RESISTANCE, 1,
            StatusEffects.SPEED, 1,
            StatusEffects.HASTE, 1
    );
    private final static Identifier RESIZE_BOSS_BAR_ID = new Identifier("battlegrounds", "resize_boss_bar");
    private final static Identifier HEALTH_MODIFIER_ID = new Identifier("battlegrounds", "game/main");
    private final static double HEALTH_MODIFIER_ADD_VALUE = 20;

    public GameManager(MinecraftServer server) {
        this.server = server;
        this.worldHelper = new WorldHelper(server.getOverworld());
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(this::giveEnrichEffects);
        ModServerPlayerEvents.PLAYER_JOINED.register(this::onPlayerJoin);
        ModServerPlayerEvents.ALLOW_NATURAL_REGEN.register(this::allowPlayerNaturalRegen);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onLivingEntityDeath);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
        ServerGameEvents.STAGE_CHANGED.register(this::onStageChanged);
        ServerGameEvents.BORDER_RESIZING.register(this::onBorderResizing);
        ServerGameEvents.PLAYER_WON.register(this::onPlayerWon);
    }

    private void onServerStopping(MinecraftServer server) {
        this.disableBossBar();
    }

    private void onBorderResizing(GameProperties.StageInfo currentStage, int resizeCount) {
        this.server.getPlayerManager().broadcast(
                Text.translatable("battlegrounds.game.border.reduce.broadcast")
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withUnderline(true)),
                false
        );
    }

    private void onStageChanged(@Nullable Identifier id) {
        if (id == null) return;
        if (id.equals(new Identifier("battlegrounds", "enable_pvp"))) {
            this.pvpMode = PVPMode.PVP_MODE;
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                UUID uuid = PlayerUtil.getAuthUUID(player);
                PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
                if (permission.inGame) {
                    permission.hasEnrichEffects = false;
                }
            }
            this.server.getPlayerManager().broadcast(
                    Text.translatable("battlegrounds.game.pvp.enable.broadcast")
                            .formatted(Formatting.GOLD)
                            .styled(style -> style.withUnderline(true)),
                    false
            );
        } else if (id.equals(new Identifier("battlegrounds", "deathmatch"))) {
            for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
                UUID uuid = PlayerUtil.getAuthUUID(player);
                PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
                if (permission.inGame) {
                    permission.allowNaturalRegen = false;
                }
            }
            this.server.getPlayerManager().broadcast(
                    Text.translatable("battlegrounds.game.deathmatch.start.broadcast")
                            .formatted(Formatting.GOLD)
                            .styled(style -> style.withBold(true).withUnderline(true)),
                    false
            );
        }
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
                        Text.translatable("battlegrounds.game.join.broadcast",
                                player.getDisplayName(),
                                this.getJoinedInGamePlayerCount(),
                                this.getTotalInGamePlayerCount()
                        )
                                .formatted(Formatting.GREEN),
                        false
                );
                if (this.checkAllPlayersJoined() && this.startTask.isCancelled()) {
                    this.startTask = new LimitRepeatTask(Duration.ZERO, Duration.withSeconds(1), 10) {
                        @Override
                        public void onTriggered() throws CancellationException {
                            PlayerUtil.broadcastTitle(server, Text.literal(String.valueOf(this.getCount()))
                                    .formatted(Formatting.GREEN));
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
                        Text.translatable("battlegrounds.game.join.spectator.broadcast",
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
        if (entity instanceof ServerPlayerEntity player && this.gameStage.isGaming()
        && this.currentStage != null) {
            if (!this.currentStage.allowRespawn()) {
                UUID uuid = PlayerUtil.getAuthUUID(player);
                this.setPlayerPermission(uuid, new PlayerPermission());
                PlayerUtil.changeGameMode(player, GameMode.SPECTATOR);
            }
            if (this.getJoinedInGamePlayerCount() == 1) {
                ServerPlayerEntity winner = this.getLastInGamePlayer();
                ServerGameEvents.PLAYER_WON.invoker().onPlayerWon(winner);
            }
        }
    }

    private void onPlayerWon(ServerPlayerEntity winner) {
        Random random = winner.getRandom();
        this.server.getPlayerManager().broadcast(
                Text.translatable("battlegrounds.game.won.broadcast", winner.getDisplayName())
                        .formatted(Formatting.GOLD)
                        .styled(style -> style.withBold(true).withUnderline(true)),
                false
        );
        int fireworkAmount = random.nextBetween(2, 4);
        GameUtil.spawnWinnerFireworks(winner, fireworkAmount, 4);
        this.stopGame();
    }

    private boolean allowLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            return this.pvpMode.isAllowDamage(source, player);
        } else return true;
    }

    private boolean allowPlayerNaturalRegen(PlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
        return permission.allowNaturalRegen;
    }

    private void giveEnrichEffects(MinecraftServer server) {
        this.playerPermissions.forEach((uuid, permission) -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null
                    && permission.hasEnrichEffects) {
                ENRICH_EFFECTS.forEach((effect, amplifier) ->
                        player.addStatusEffect(new StatusEffectInstance(effect, 2, amplifier)));
            }
        });
    }

    public void assertGamePropertiesNotNull() {
        Objects.requireNonNull(this.gameProperties);
    }
    public void assertIsGaming() {
        if (!this.gameStage.isGaming()) throw new IllegalStateException();
    }

    public void assertIsNotGaming() {
        if (this.gameStage.isGaming()) throw new IllegalStateException();
    }

    public void assertBorderResizeEnabled() {
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

    public PVPMode getPvpMode() {
        return pvpMode;
    }

    public GameProperties getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(@NotNull GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    public int getJoinedInGamePlayerCount() {
        AtomicInteger count = new AtomicInteger();
        this.playerPermissions.forEach((uuid, permission) -> {
            if (permission.inGame && this.server.getPlayerManager().getPlayer(uuid) != null) count.getAndIncrement();
        });
        return count.get();
    }

    public int getTotalInGamePlayerCount() {
        AtomicInteger count = new AtomicInteger();
        this.playerPermissions.forEach((key, value) -> {
            if (value.inGame) count.getAndIncrement();
        });
        return count.get();
    }

    public ServerPlayerEntity getLastInGamePlayer() {
        AtomicReference<ServerPlayerEntity> result = new AtomicReference<>(null);
        this.playerPermissions.forEach((uuid, permission) -> {
            ServerPlayerEntity p = this.server.getPlayerManager().getPlayer(uuid);
            if (permission.inGame && p != null) {
                result.set(p);
            }
        });
        return result.get();
    }

    public boolean isParticipant(UUID uuid) {
        AtomicBoolean bl = new AtomicBoolean(false);
        this.playerPermissions.forEach((key, value) -> {
            if (key.equals(uuid) && value.inGame && !bl.get()) bl.set(true);
        });
        return bl.get();
    }

    public boolean checkAllPlayersJoined() {
        return this.getJoinedInGamePlayerCount() == this.getTotalInGamePlayerCount();
    }

    public void enableBossBar() {
        this.assertIsGaming();
        this.assertBorderResizeEnabled();
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bossBar = manager.get(RESIZE_BOSS_BAR_ID);
        if (bossBar == null) {
            bossBar = manager.add(RESIZE_BOSS_BAR_ID, Text.empty());
        }
        bossBar.clearPlayers();
        bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
        this.resizeBossBar = bossBar;
        this.updateBossBar();
        this.updateBossBarTask = new RepeatTask(Duration.withTicks(this.resizeBorderTask.getDelayTicks() % 20), Duration.withSeconds(1)) {
            @Override
            public void onTriggered() throws CancellationException {
                GameManager.this.updateBossBar();
            }
        };
        TaskScheduler.INSTANCE.schedule(this.updateBossBarTask);
    }

    public void disableBossBar() {
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

    private void updateBossBar() {
        if (!this.gameStage.isGaming()) return;
        int totalTime = this.currentStage.resizeTimeInfo().spendTime().add(this.currentStage.resizeTimeInfo().delayTime()).toTicks();
        this.resizeBossBar.setName(Text.translatable(
                "battlegrounds.resize_border.bar",
                this.resizeBorderTask.getDelayTicks() / 20
        )
                .formatted(Formatting.GREEN));
        this.resizeBossBar.setMaxValue(totalTime);
        this.resizeBossBar.setValue(this.resizeBorderTask.getDelayTicks());
    }

    public void setIdleState() {
        this.gameStage = GameStage.IDLE;
        this.pvpMode = PVPMode.PEACEFUL;
        this.worldHelper.setDefaultBorder();
    }

    public void prepareToDeleteWorld(Collection<UUID> participants) {
        this.assertGamePropertiesNotNull();
        this.server.getPlayerManager().broadcast(
                Text.translatable("battlegrounds.game.delete_world.broadcast", 10)
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
                        SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(),
                        SoundCategory.NEUTRAL, 0.7F, 1.0F);
            }

            @Override
            public void onCompleted() throws CancellationException {
                GameManager.this.stopServerToDeleteWorld(participants);
            }
        };
        TaskScheduler.INSTANCE.schedule(stopTask);
    }

    public void stopServerToDeleteWorld(Collection<UUID> participants) {
        this.assertGamePropertiesNotNull();
        this.playerPermissions.clear();
        for (UUID uuid : participants) {
            PlayerPermission permission = new PlayerPermission();
            permission.inGame = true;
            this.setPlayerPermission(uuid, permission);
        }
        this.gameStage = GameStage.WAITING_PLAYER;
        GameUtil.createDeleteWorldTmpFile(this.server.getSavePath(WorldSavePath.ROOT));
        PlayerUtil.kickAllPlayers(this.server, Text.translatable("battlegrounds.game.server.stop")
                .formatted(Formatting.GREEN));

        this.server.stop(false);
    }

    public void startGame(){
        this.assertIsNotGaming();
        this.assertGamePropertiesNotNull();
        this.currentStage = gameProperties.stages().get(0);
        World world = this.server.getOverworld();
        BlockPos borderCenter = RandomUtil.getSecureLocation(world);
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            UUID uuid = PlayerUtil.getAuthUUID(player);
            PlayerPermission permission = this.getPlayerPermission(uuid, new PlayerPermission());
            if (permission.inGame) {
                permission.hasEnrichEffects = true;
                PlayerUtil.changeGameMode(player, GameMode.SURVIVAL);
                EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                assert attributeInstance != null;
                EffectUtil.addAttribute(attributeInstance, HEALTH_MODIFIER_ID, HEALTH_MODIFIER_ADD_VALUE, EntityAttributeModifier.Operation.ADDITION);
                player.heal((float) HEALTH_MODIFIER_ADD_VALUE);
            }
        }
        this.gameStage = GameStage.GAMING;
        this.pvpMode = PVPMode.NO_PVP;
        this.worldHelper.setBorderCenter(borderCenter.getX(), borderCenter.getZ());
        this.worldHelper.setBorderSize(this.currentStage.initialSize());
        PlayerUtil.randomTpAllPlayers(this.server, this.server.getOverworld());
        this.enableBorderResizing(this.currentStage.resizeTimeInfo().delayTime().toTicks());
        this.enableBossBar();
        ServerGameEvents.STAGE_CHANGED.invoker().onStageChanged(this.currentStage.trigger());
    }

    public void stopGame() {
        this.assertIsGaming();
        this.currentStage = null;
        this.disableBossBar();
        this.disableBorderResizing();
        this.setIdleState();
        this.playerPermissions.clear();
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            player.getInventory().clear();
            PlayerUtil.changeGameMode(player, GameMode.ADVENTURE);
            EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            assert attributeInstance != null;
            EffectUtil.removeAttribute(attributeInstance, HEALTH_MODIFIER_ID);
        }
        ServerGameEvents.STAGE_CHANGED.invoker().onStageChanged(null);
    }

    public void resumeGame(int resizeTimer) {
        this.assertIsGaming();
        this.assertGamePropertiesNotNull();
        this.enableBorderResizing(resizeTimer);
        this.enableBossBar();
    }

    public void enableBorderResizing(int delayTicks) {
        this.assertIsGaming();
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
        if (this.resizeBorderTask != null) {
            this.resizeBorderTask.cancel();
        }
    }

    private void resizeBorder() {
        this.assertIsGaming();
        Duration spendTime = this.currentStage.resizeTimeInfo().spendTime();
        if (this.resizeCount == 0) {
            this.worldHelper.setBorderSize(this.currentStage.initialSize());
        }
        this.worldHelper.setBorderSize(
                this.currentStage.initialSize() - (this.currentStage.resizeBlocks() * (this.resizeCount + 1)),
                spendTime.toMillis()
        );
        this.resizeCount++;
        ServerGameEvents.BORDER_RESIZING.invoker().onBorderResizing(this.currentStage, this.resizeCount);
        if (this.resizeCount >= this.currentStage.resizeCount()) {
            ScheduledTask triggerTask = new ScheduledTask(spendTime) {
                @Override
                public void run() throws CancellationException {
                    int currentStageIdx = GameManager.this.gameProperties.stages().indexOf(GameManager.this.currentStage);
                    if (currentStageIdx == GameManager.this.gameProperties.stages().size() - 1) {
                        GameManager.this.disableBorderResizing();
                        GameManager.this.disableBossBar();
                    } else {
                        GameManager.this.currentStage = GameManager.this.gameProperties.stages().get(currentStageIdx + 1);
                        int oldSize = GameManager.this.worldHelper.getBorderSize();
                        GameManager.this.worldHelper.setBorderSize(GameManager.this.currentStage.initialSize());
                        if (oldSize - GameManager.this.currentStage.initialSize() > GameManager.this.worldHelper.getBorder().getSafeZone()) {
                            PlayerUtil.randomTpAllPlayers(GameManager.this.server, GameManager.this.server.getOverworld());
                        }
                        ServerGameEvents.STAGE_CHANGED.invoker().onStageChanged(GameManager.this.currentStage.trigger());
                    }
                    GameManager.this.resizeCount = 0;
                }
            };
            TaskScheduler.INSTANCE.schedule(triggerTask);
        }
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
        NbtList playerPermissionsList = (NbtList) nbt.get("player_permissions");
        Objects.requireNonNull(playerPermissionsList);
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
            int timer = Codec.INT.parse(NbtOps.INSTANCE, nbt.get("resize_timer"))
                    .result().orElse(0);
            manager.resumeGame(timer);
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("resize_count", this.resizeCount);
        nbt.put("pvp_mode", PVPMode.CODEC.encodeStart(NbtOps.INSTANCE, this.pvpMode)
                .getOrThrow(false, Constants.LOGGER::error));
        nbt.put("game_stage", GameStage.CODEC.encodeStart(NbtOps.INSTANCE, this.gameStage)
                .getOrThrow(false, Constants.LOGGER::error));
        NbtList playerPermissionsList = new NbtList();
        this.playerPermissions.forEach((uuid, permission) -> {
            NbtCompound permissionNbt = permission.toNbt();
            permissionNbt.putUuid("uuid", uuid);
            playerPermissionsList.add(permissionNbt);
        });
        nbt.put("player_permissions", playerPermissionsList);
        if (this.gameProperties != null) {
            nbt.put("game_properties", GameProperties.CODEC.encodeStart(NbtOps.INSTANCE, this.gameProperties)
                    .getOrThrow(false, Constants.LOGGER::error));
        }
        if (this.currentStage != null) {
            nbt.put("current_stage", GameProperties.StageInfo.CODEC.encodeStart(NbtOps.INSTANCE, this.currentStage)
                    .getOrThrow(false, Constants.LOGGER::error));
        }
        if (this.gameStage.isGaming()) {
            nbt.putInt("resize_timer", this.resizeBorderTask.getDelayTicks());
        }
        return nbt;
    }
}
