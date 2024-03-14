package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.commands.BattlegroundsCommand;
import com.github.winexp.battlegrounds.commands.RandomTpCommand;
import com.github.winexp.battlegrounds.configs.GameProgress;
import com.github.winexp.battlegrounds.configs.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.events.ModServerPlayerEvents;
import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.item.ItemGroups;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTableModifier;
import com.github.winexp.battlegrounds.network.ModServerNetworkPlayHandler;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.ConfigUtil;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

public class Battlegrounds implements ModInitializer {
    public static Battlegrounds INSTANCE;

    public void saveConfigs() {
        ConfigUtil.saveConfig(Constants.CONFIG_PATH, "config", Variables.config);
        if (Variables.server != null) {
            if (GameManager.INSTANCE.reduceTask.getDelay() >= 0) {
                Variables.progress.resizeLapTimer = GameManager.INSTANCE.reduceTask.getDelay();
            }
            ConfigUtil.saveConfig(Variables.server.getSavePath(WorldSavePath.ROOT), "bg_progress", Variables.progress);
        }
    }

    public void loadConfigs() {
        Variables.config = ConfigUtil.createOrLoadConfig(Constants.CONFIG_PATH, "config", RootConfig.class);
        if (Variables.server != null) {
            Variables.progress = ConfigUtil.createOrLoadConfig(Variables.server.getSavePath(WorldSavePath.ROOT),
                    "bg_progress",
                    GameProgress.class);
        }
    }

    public void reload() {
        this.loadConfigs();
        Items.addCustomRecipes();
        GameManager.INSTANCE.reduceTask.cancel();
        if (Variables.progress.gameStage.isStarted() && !Variables.progress.gameStage.isDeathmatch()) {
            GameManager.INSTANCE.runTasks();
        }
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        BattlegroundsCommand.registerRoot(dispatcher);
        RandomTpCommand.register(dispatcher);
    }

    private void onServerStarting(MinecraftServer server) {
        Variables.server = server;
        GameManager.INSTANCE.setServer(server);
        this.loadConfigs();
    }

    private void onServerStarted(MinecraftServer server) {
        GameManager.INSTANCE.initialize();
    }

    private void onSaving(MinecraftServer server, boolean flush, boolean force) {
        this.saveConfigs();
    }

    private void onPlayerRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        PlayerUtil.setGameModeWithMap(newPlayer);
    }

    private boolean allowLivingEntityDamaged(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof ServerPlayerEntity player) {
            if (Variables.progress.pvpMode == GameProgress.PVPMode.PEACEFUL) {
                return false;
            } else if (Variables.progress.pvpMode == GameProgress.PVPMode.NO_PVP) {
                if (source.getSource() != null && source.getSource().isPlayer()) {
                    return false;
                }
            }
            if (Variables.progress.gameStage.isStarted() && source.getSource() != null
                    && source.getSource().isPlayer() && Variables.progress.isInGame(PlayerUtil.getUUID(player))) {
                RandomTpCommand.setCooldown(player, Variables.config.cooldown.randomTpDamagedCooldownTicks);
            }
        }
        return true;
    }

    private void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        Text joinText = Text.empty();
        if (Variables.progress.gameStage.isPreparing()) {
            GameProgress.PlayerPermission permission = Variables.progress.players.get(PlayerUtil.getUUID(player));
            if (permission != null && !permission.inGame) {
                joinText = Text.translatable("battlegrounds.game.join.spectator.broadcast", player.getDisplayName())
                        .formatted(Formatting.DARK_GRAY);
            } else {
                joinText = Text.translatable("battlegrounds.game.join.broadcast", player.getDisplayName(),
                        Variables.server.getPlayerManager().getCurrentPlayerCount() + 1, Variables.progress.players.size())
                        .formatted(Formatting.GREEN);
                Variables.server.getPlayerManager().getWhitelist().add(new WhitelistEntry(player.getGameProfile()));
                if (Variables.server.getPlayerManager().getPlayerList().size() + 1 == Variables.progress.players.size()) {
                    GameManager.INSTANCE.prepareStartGame();
                }
            }
        } else if (Variables.progress.gameStage.isDeathmatch()) {
            player.sendMessage(Text.translatable("battlegrounds.game.deathmatch.start.broadcast")
                    .formatted(Formatting.GOLD), false);
        }
        server.getPlayerManager().broadcast(joinText, false);
        PlayerUtil.setGameModeWithMap(player);
    }

    private boolean allowPlayerNaturalRegen(PlayerEntity player) {
        if (player.getWorld().isClient) return true;
        GameProgress.PlayerPermission permission = Variables.progress.players.get(PlayerUtil.getUUID(player));
        return permission == null || permission.naturalRegen;
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Constants.LOGGER.info("Loading {}", Constants.MOD_ID);
        // 加载配置
        this.loadConfigs();
        // 注册事件
        // 指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.AFTER_SAVE.register(this::onSaving);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::allowLivingEntityDamaged);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);
        LootTableEvents.MODIFY.register(new LootTableModifier());
        // 自定义事件
        ModServerPlayerEvents.ALLOW_NATURAL_REGEN.register(this::allowPlayerNaturalRegen);
        // 注册网络包接收器
        ModServerNetworkPlayHandler.registerReceivers();
        // 注册物品
        Items.registerItems();
        // 注册物品组
        ItemGroups.registerItemGroups();
        // 注册实体
        EntityTypes.registerEntityTypes();
        // 注册附魔
        Enchantments.registerEnchantments();
        Enchantments.SMELTING.registerDefaultSmeltable(); // 注册自动冶炼方块
        // 注册声音事件
        SoundEvents.registerSoundEvents();
        // 尝试重置存档
        GameManager.INSTANCE.tryResetWorlds();
    }
}
