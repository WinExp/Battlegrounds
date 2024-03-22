package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.block.BlockSmeltableRegistry;
import com.github.winexp.battlegrounds.command.BattlegroundsCommand;
import com.github.winexp.battlegrounds.command.RandomTpCommand;
import com.github.winexp.battlegrounds.config.ConfigUtil;
import com.github.winexp.battlegrounds.config.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.game.*;
import com.github.winexp.battlegrounds.item.ItemGroups;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTableModifier;
import com.github.winexp.battlegrounds.network.ModServerNetworkPlayHandler;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.*;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

public class Battlegrounds implements ModInitializer {
    public static Battlegrounds INSTANCE;
    public static final HashMap<Identifier, GameProperties> GAME_PRESETS = new HashMap<>();

    public void loadGamePresets() {
        this.saveGamePresets();
        GAME_PRESETS.clear();
        for (File directory : FileUtil.listFiles(Constants.CONFIG_PATH.resolve("presets"))) {
            if (directory.isDirectory()) {
                for (File file : FileUtil.listFiles(directory.toPath())) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        try {
                            JsonElement json = Constants.GSON.fromJson(FileUtil.readString(file), JsonElement.class);
                            GameProperties preset = GameProperties.CODEC.parse(JsonOps.INSTANCE, json)
                                    .getOrThrow(false, Constants.LOGGER::error);
                            GAME_PRESETS.put(preset.id(), preset);
                        } catch (Exception e) {
                            Constants.LOGGER.error("无法将 %s 转换为 GamePreset：".formatted(file.toString()), e);
                        }
                    }
                }
            }
        }
    }

    public void saveGamePresets() {
        List<GameProperties> presets = List.of(
                GameProperties.NORMAL_PRESET
        );
        for (GameProperties preset : presets) {
            Identifier id = preset.id();
            File file = Constants.CONFIG_PATH.resolve("presets")
                    .resolve(id.getNamespace())
                    .resolve(id.getPath() + ".json").toFile();
            if (!file.isFile()) {
                JsonElement json = GameProperties.CODEC.encodeStart(JsonOps.INSTANCE, preset)
                        .getOrThrow(false, Constants.LOGGER::error);
                String jsonStr = Constants.GSON.toJson(json);
                try {
                    FileUtil.writeString(file, jsonStr);
                } catch (Exception e) {
                    Constants.LOGGER.error("无法将 GamePreset 写入文件 %s：".formatted(file.toString()), e);
                }
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
        this.loadGamePresets();

        Items.addCustomRecipes();
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        BattlegroundsCommand.registerRoot(dispatcher);
        RandomTpCommand.register(dispatcher);
    }

    private void onServerStarting(MinecraftServer server) {
        Variables.server = server;
        this.loadConfigs();
    }

    private void onServerStarted(MinecraftServer server) {
        Variables.gameManager = GameManager.getManager(server);
    }

    private void onSaving(MinecraftServer server, boolean flush, boolean force) {
        this.loadConfigs();
    }

    private void tryDeleteWorld() {
        if (Files.isRegularFile(Constants.DELETE_WORLD_TMP_FILE_PATH)) {
            GameUtil.deleteWorld();
            FileUtil.delete(Constants.DELETE_WORLD_TMP_FILE_PATH, true);
            Constants.LOGGER.info("已重置地图");
        }
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Constants.LOGGER.info("Loading {}", Constants.MOD_ID);
        // 加载配置
        this.loadConfigs();
        this.loadGamePresets(); // 加载游戏预设
        // 注册事件
        // 指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.AFTER_SAVE.register(this::onSaving);
        LootTableEvents.MODIFY.register(new LootTableModifier());
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
        // 自动冶炼
        BlockSmeltableRegistry.registerDefaults();
        // 注册声音事件
        SoundEvents.registerSoundEvents();
        // 尝试重置存档
        this.tryDeleteWorld();
    }
}
