package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.block.BlockSmeltableRegistry;
import com.github.winexp.battlegrounds.command.BattlegroundsCommand;
import com.github.winexp.battlegrounds.command.RandomTpCommand;
import com.github.winexp.battlegrounds.config.ConfigUtil;
import com.github.winexp.battlegrounds.config.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.game.GameProperties;
import com.github.winexp.battlegrounds.game.GameUtil;
import com.github.winexp.battlegrounds.item.ItemGroups;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTableModifier;
import com.github.winexp.battlegrounds.network.ModServerConfigurationNetworkHandler;
import com.github.winexp.battlegrounds.network.ModServerPlayNetworkHandler;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Battlegrounds implements ModInitializer {
    public static Battlegrounds INSTANCE;
    public static final HashMap<Identifier, GameProperties> GAME_PRESETS = new HashMap<>();

    public void loadGameProperties() {
        this.saveGameProperties();
        GAME_PRESETS.clear();
        try {
            for (File directory : FileUtil.listFiles(Constants.GAME_PROPERTIES_PATH)) {
                if (directory.isDirectory()) {
                    for (File file : FileUtil.listFiles(directory.toPath())) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            try {
                                JsonElement json = Constants.GSON.fromJson(FileUtil.readString(file), JsonElement.class);
                                GameProperties properties = GameProperties.CODEC.parse(JsonOps.INSTANCE, json).result().orElseThrow();
                                GAME_PRESETS.put(properties.id(), properties);
                            } catch (Exception e) {
                                Constants.LOGGER.warn("无法将 %s 转换为游戏配置：".formatted(file.toString()), e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Constants.LOGGER.warn("无法加载游戏配置：", e);
        }
    }

    public void saveGameProperties() {
        List<GameProperties> properties = List.of(
                GameProperties.NORMAL_PRESET
        );
        for (GameProperties preset : properties) {
            Identifier id = preset.id();
            File file = Constants.GAME_PROPERTIES_PATH
                    .resolve(id.getNamespace())
                    .resolve(id.getPath() + ".json").toFile();
            try {
                Optional<GameProperties> parseResult = Optional.empty();
                if (file.exists()) {
                    JsonElement jsonFrom = Constants.GSON.fromJson(FileUtil.readString(file), JsonElement.class);
                    parseResult = GameProperties.CODEC.parse(JsonOps.INSTANCE, jsonFrom).result();
                }
                if (parseResult.isEmpty()) {
                    JsonElement json = GameProperties.CODEC.encodeStart(JsonOps.INSTANCE, preset)
                            .getOrThrow(false, Constants.LOGGER::error);
                    String jsonStr = Constants.GSON.toJson(json);
                    FileUtil.writeString(file, jsonStr);
                }
            } catch (Exception e) {
                Constants.LOGGER.error("无法将游戏配置写入文件 %s：".formatted(file.toString()), e);
            }
        }
    }

    public void loadConfigs() {
        Variables.config = ConfigUtil.readOrCreateConfig(
                Constants.CONFIG_PATH, "config",
                RootConfig.CODEC, RootConfig.DEFAULT_CONFIG
        );
    }

    public void reload() {
        this.loadConfigs();
        this.loadGameProperties();

        Items.addCustomRecipes();
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        BattlegroundsCommand.registerRoot(dispatcher);
        RandomTpCommand.register(dispatcher);
    }

    private void onPlayInit(ServerPlayNetworkHandler handler, MinecraftServer server) {

    }

    private void onServerStarting(MinecraftServer server) {
        Variables.server = server;
        this.loadConfigs();
    }

    private void onServerStarted(MinecraftServer server) {
        Variables.gameManager = GameManager.getManager(server);
    }

    private void onServerStopping(MinecraftServer server) {
        TaskScheduler.INSTANCE.cancelAllTasks();
    }

    private void onSaving(MinecraftServer server, boolean flush, boolean force) {
        this.loadConfigs();
    }

    private void tryDeleteWorld() {
        if (Constants.DELETE_WORLD_TMP_FILE_PATH.toFile().isFile()) {
            GameUtil.deleteWorld();
            FileUtil.delete(Constants.DELETE_WORLD_TMP_FILE_PATH, true);
            Constants.LOGGER.info("已重置地图");
        }
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Constants.LOGGER.info("Loading {}", Constants.MOD_NAME);
        // 加载配置
        this.loadConfigs();
        this.loadGameProperties(); // 加载游戏预设
        // 注册事件
        // 指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerLifecycleEvents.AFTER_SAVE.register(this::onSaving);
        LootTableEvents.MODIFY.register(new LootTableModifier());
        ServerPlayConnectionEvents.INIT.register(this::onPlayInit);
        // 注册网络包相关
        ModServerConfigurationNetworkHandler.registerReceivers();
        ModServerPlayNetworkHandler.register();
        // 注册物品
        Items.registerItems();
        // 注册物品组
        ItemGroups.registerItemGroups();
        // 注册实体
        EntityTypes.registerEntityTypes();
        // 注册附魔
        Enchantments.registerEnchantments();
        // 自动冶炼
        BlockSmeltableRegistry.registerDefaults();
        // 注册声音事件
        SoundEvents.registerSoundEvents();
        // 尝试重置存档
        this.tryDeleteWorld();
    }
}
