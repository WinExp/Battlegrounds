package com.github.winexp.battlegrounds.game;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.event.ServerGameEvents;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.task.LimitRepeatTask;
import com.github.winexp.battlegrounds.task.RepeatTask;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager extends PersistentState {
    public final MinecraftServer server;
    private final WorldHelper worldHelper;
    private final HashMap<UUID, PlayerPermission> playerPermissions = new HashMap<>();
    private PVPMode pvpMode = PVPMode.PEACEFUL;
    private GameStage gameStage = GameStage.IDLE;
    private GameProperties gameProperties;
    private GameProperties.StageInfo currentStage;
    private int resizeCount = 0;
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

    public GameManager(MinecraftServer server) {
        this.server = server;
        this.worldHelper = new WorldHelper(server.getOverworld());
        ServerTickEvents.END_SERVER_TICK.register(this::giveEnrichEffects);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamage);
        ServerGameEvents.STAGE_CHANGED.register(this::onStageChanged);
        ModServerPlayerEvents.ALLOW_NATURAL_REGEN.register(this::allowPlayerNaturalRegen);
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

    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        UUID uuid = PlayerUtil.getAuthUUID(player);
        if (this.getGameStage() == GameStage.WAITING_PLAYER) {
            if (this.isParticipant(uuid)) {
                server.getPlayerManager().broadcast(
                        Text.translatable("battlegrounds.game.join.broadcast",
                                player.getDisplayName(),
                                this.getJoinedInGamePlayerCount(),
                                this.getTotalInGamePlayerCount()
                        ),
                        false
                );
            } else {
                server.getPlayerManager().broadcast(
                        Text.translatable("battlegrounds.game.join.spectator.broadcast",
                                player.getDisplayName()
                        ),
                        false
                );
            }
            if (this.getJoinedInGamePlayerCount() == this.getTotalInGamePlayerCount()) {
                LimitRepeatTask startTask = new LimitRepeatTask(0, 20, 10) {
                    @Override
                    public void onTriggered() throws CancellationException {
                        PlayerUtil.broadcastTitle(server, Text.of(String.valueOf(this.getCount())));
                    }

                    @Override
                    public void onCompleted() throws CancellationException {
                        GameManager.this.startGame();
                    }
                };
                TaskScheduler.INSTANCE.schedule(startTask);
            }
        }
    }

    private boolean allowLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        return this.pvpMode.isAllowDamage(source, entity);
    }

    private boolean allowPlayerNaturalRegen(PlayerEntity player) {
        UUID uuid = PlayerUtil.getAuthUUID(player);
        PlayerPermission permission = this.playerPermissions.getOrDefault(uuid, new PlayerPermission());
        return permission.allowNaturalRegen;
    }

    public void assertGamePropertiesNotNull() {
        Objects.requireNonNull(this.gameProperties);
    }
    public void assertIsGaming() {
        if (!this.gameStage.isGaming()) throw new IllegalStateException("当前并未开始游戏");
    }

    public void assertIsNotGaming() {
        if (this.gameStage.isGaming()) throw new IllegalStateException("当前已经开始游戏");
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public PlayerPermission getPlayerPermission(UUID uuid) {
        return this.getPlayerPermission(uuid, null);
    }

    public void setPlayerPermission(UUID uuid, PlayerPermission permission) {
        this.playerPermissions.put(uuid, permission);
    }

    public PlayerPermission getPlayerPermission(UUID uuid, PlayerPermission defaultValue) {
        return this.playerPermissions.getOrDefault(uuid, defaultValue);
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

    public boolean isParticipant(UUID uuid) {
        AtomicBoolean bl = new AtomicBoolean(false);
        this.playerPermissions.forEach((key, value) -> {
            if (key.equals(uuid) && value.inGame && !bl.get()) bl.set(true);
        });
        return bl.get();
    }

    public boolean checkAllPlayersJoined() {
        AtomicBoolean bl = new AtomicBoolean(true);
        this.playerPermissions.forEach((key, value) -> {
            ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(key);
            if (player == null && bl.get()) bl.set(false);
        });
        return bl.get();
    }

    public void enableBossBar() {
        this.assertIsGaming();
        BossBarManager manager = this.server.getBossBarManager();
        CommandBossBar bossBar = manager.get(RESIZE_BOSS_BAR_ID);
        if (bossBar == null) {
            bossBar = manager.add(RESIZE_BOSS_BAR_ID, Text.translatable("battlegrounds.resize_border.bar"));
        }
        bossBar.clearPlayers();
        bossBar.addPlayers(this.server.getPlayerManager().getPlayerList());
        this.resizeBossBar = bossBar;
        this.updateBossBarTask = new RepeatTask(this.resizeBorderTask.getDelay() % 20, 20) {
            @Override
            public void onTriggered() throws CancellationException {
                GameManager.this.updateBossBar();
            }
        };
        TaskScheduler.INSTANCE.schedule(this.updateBossBarTask);
    }

    public void disableBossBar() {
        BossBarManager manager = this.server.getBossBarManager();
        if (this.resizeBossBar != null && this.resizeBossBar != manager.get(RESIZE_BOSS_BAR_ID)) {
            this.enableBossBar();
        }
        if (this.resizeBossBar == null) return;
        this.resizeBossBar.clearPlayers();
        this.updateBossBarTask.cancel();
        manager.remove(this.resizeBossBar);
    }

    private void updateBossBar() {
        this.assertIsGaming();
        int totalTime = this.currentStage.timeInfo().spendTime().toSeconds() + this.currentStage.timeInfo().delayTime().toSeconds();
        this.resizeBossBar.setMaxValue(totalTime);
        this.resizeBossBar.setValue(totalTime - this.resizeBorderTask.getDelay());
    }

    public void setIdleState() {
        this.disableBorderResizing();
        this.disableBossBar();
        this.gameStage = GameStage.IDLE;
        this.pvpMode = PVPMode.PEACEFUL;
        this.worldHelper.setDefaultBorder();
    }

    public void prepareToDeleteWorld(Collection<UUID> participants) {
        this.server.getPlayerManager().broadcast(
                Text.translatable("battlegrounds.game.delete_world.broadcast", 10)
                        .formatted(Formatting.GREEN),
                false
        );
        LimitRepeatTask stopTask = new LimitRepeatTask(0, 20, 10) {
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
        this.playerPermissions.clear();
        for (UUID uuid : participants) {
            PlayerPermission permission = new PlayerPermission();
            permission.inGame = true;
            this.playerPermissions.put(uuid, permission);
        }
        this.gameStage = GameStage.DELETING_WORLD;
        GameUtil.createDeleteWorldTmpFile(this.server.getSavePath(WorldSavePath.ROOT));
        PlayerUtil.kickAllPlayers(this.server, Text.translatable("battlegrounds.game.server.stop")
                .formatted(Formatting.GREEN));
        this.server.exit();
    }

    public void startGame(){
        this.assertIsNotGaming();
        this.assertGamePropertiesNotNull();
        if (!this.checkAllPlayersJoined()) {
            Constants.LOGGER.error("无法开始游戏：有玩家未加入游戏");
        }
        this.currentStage = gameProperties.stages().get(0);
        World world = this.server.getOverworld();
        BlockPos borderCenter = RandomUtil.getSecureLocation(world);
        this.worldHelper.setBorderCenter(borderCenter.getX(), borderCenter.getZ());
        this.worldHelper.setBorderSize(this.currentStage.initialSize());
        for (ServerPlayerEntity player : this.server.getPlayerManager().getPlayerList()) {
            PlayerUtil.randomTeleport(world, player);
        }
        this.gameStage = GameStage.GAMING;
        this.pvpMode = PVPMode.NO_PVP;
        this.enableBorderResizing();
        this.enableBossBar();
    }

    public void stopGame() {
        this.assertIsGaming();
        this.currentStage = null;
        this.setIdleState();
        ServerGameEvents.STAGE_CHANGED.invoker().onStageChanged(null);
    }

    public void enableBorderResizing() {
        this.assertIsGaming();
        GameProperties.StageInfo.TimeInfo timeInfo = this.currentStage.timeInfo();
        this.resizeBorderTask = new RepeatTask(timeInfo.delayTime().toTicks(),
                timeInfo.delayTime().toTicks() + timeInfo.spendTime().toTicks()) {
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

    private void onStageChanged(Identifier id) {
        this.assertIsGaming();
        if (id.equals(new Identifier("battlegrounds", "enable_pvp"))) {
            this.pvpMode = PVPMode.PVP_MODE;
        } else if (id.equals(new Identifier("battlegrounds", "deathmatch"))) {

        }
    }

    private void resizeBorder() {
        this.assertIsGaming();
        if (this.resizeCount == 0) {
            this.worldHelper.setBorderSize(this.currentStage.initialSize());
        }
        this.worldHelper.setBorderSize(
                this.currentStage.initialSize() - (this.currentStage.resizeBlocks() * (this.resizeCount + 1)),
                this.currentStage.timeInfo().spendTime().toTicks()
        );
        this.resizeCount++;
        if (this.resizeCount >= this.currentStage.resizeCount()) {
            int currentStageIdx = this.gameProperties.stages().indexOf(this.currentStage);
            ServerGameEvents.STAGE_CHANGED.invoker().onStageChanged(this.currentStage.id());
            if (currentStageIdx == this.gameProperties.stages().size() - 1) {
                this.disableBorderResizing();
                this.disableBossBar();
            } else {
                this.currentStage = this.gameProperties.stages().get(currentStageIdx + 1);
            }
            this.resizeCount = 0;
        }
    }

    public static PersistentState.Type<GameManager> getPersistentStateType(MinecraftServer server) {
        return new Type<>(() -> {
            GameManager manager = new GameManager(server);
            manager.setGameProperties(GameProperties.NORMAL_PRESET);
            return manager;
        }, nbt -> createFromNbt(server, nbt), null);
    }

    public static GameManager getManager(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        GameManager gameManager = manager.getOrCreate(getPersistentStateType(server), PERSISTENT_STATE_ID);
        gameManager.markDirty();
        return gameManager;
    }

    public static GameManager createFromNbt(MinecraftServer server, NbtCompound nbt) {
        GameManager manager = new GameManager(server);
        manager.resizeCount = nbt.getInt("resize_count");
        manager.pvpMode = PVPMode.CODEC.parse(NbtOps.INSTANCE, nbt.get("pvp_mode"))
                .getOrThrow(false, Constants.LOGGER::error);
        manager.gameStage = GameStage.CODEC.parse(NbtOps.INSTANCE, nbt.get("game_stage"))
                .getOrThrow(false, Constants.LOGGER::error);
        NbtList playerPermissionsList = (NbtList) nbt.get("player_permissions");
        Objects.requireNonNull(playerPermissionsList);
        for (NbtElement element : playerPermissionsList) {
            NbtCompound permissionNbt = (NbtCompound) element;
            UUID uuid = permissionNbt.getUuid("uuid");
            PlayerPermission permission = PlayerPermission.createFromNbt(permissionNbt);
            manager.playerPermissions.put(uuid, permission);
        }
        manager.gameProperties = GameProperties.CODEC.parse(NbtOps.INSTANCE, nbt.get("active_preset"))
                .getOrThrow(false, Constants.LOGGER::error);
        if (nbt.contains("current_stage")) {
            manager.currentStage = GameProperties.StageInfo.CODEC.parse(NbtOps.INSTANCE, nbt.get("current_stage"))
                    .getOrThrow(false, Constants.LOGGER::error);
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        this.assertGamePropertiesNotNull();
        nbt.putInt("resize_count", this.resizeCount);
        nbt.put("pvp_mode", PVPMode.CODEC.encodeStart(NbtOps.INSTANCE, this.pvpMode)
                .getOrThrow(false, Constants.LOGGER::error));
        nbt.put("game_stage", GameStage.CODEC.encodeStart(NbtOps.INSTANCE, this.gameStage)
                .getOrThrow(false, Constants.LOGGER::error));
        NbtList playerPermissionsList = new NbtList();
        this.playerPermissions.forEach((uuid, permission) -> {
            NbtCompound permissionNbt = permission.toNbt();
            playerPermissionsList.add(permissionNbt);
        });
        nbt.put("player_permissions", playerPermissionsList);
        nbt.put("active_preset", GameProperties.CODEC.encodeStart(NbtOps.INSTANCE, this.gameProperties)
                .getOrThrow(false, Constants.LOGGER::error));
        if (this.currentStage != null) {
            nbt.put("current_stage", GameProperties.StageInfo.CODEC.encodeStart(NbtOps.INSTANCE, this.currentStage)
                    .getOrThrow(false, Constants.LOGGER::error));
        }
        return nbt;
    }
}
