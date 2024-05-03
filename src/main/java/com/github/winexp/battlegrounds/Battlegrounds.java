package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.block.SmeltableBlockRegistry;
import com.github.winexp.battlegrounds.command.BattlegroundsCommand;
import com.github.winexp.battlegrounds.command.RandomTpCommand;
import com.github.winexp.battlegrounds.command.argument.PVPModeArgumentType;
import com.github.winexp.battlegrounds.config.ConfigUtil;
import com.github.winexp.battlegrounds.config.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.game.GameManager;
import com.github.winexp.battlegrounds.game.GameUtil;
import com.github.winexp.battlegrounds.item.ItemGroups;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTableModifier;
import com.github.winexp.battlegrounds.loot.function.LootFunctionTypes;
import com.github.winexp.battlegrounds.mixin.ArgumentTypesInvoker;
import com.github.winexp.battlegrounds.network.ModServerConfigurationNetworkHandler;
import com.github.winexp.battlegrounds.network.ModServerPlayNetworkHandler;
import com.github.winexp.battlegrounds.resource.listener.DataPackResourceReloadListener;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.FileUtil;
import com.github.winexp.battlegrounds.util.Variables;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Battlegrounds implements ModInitializer {
    public static Battlegrounds INSTANCE;

    public void loadConfigs() {
        Variables.config = ConfigUtil.readOrCreateConfig(
                Constants.CONFIG_PATH, "config",
                RootConfig.CODEC, RootConfig.DEFAULT_CONFIG
        );
    }

    public void reload() {
        this.loadConfigs();

        Items.addRecipes();
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

    private void registerCommandArgumentTypes() {
        ArgumentTypesInvoker.invokeRegister(
                Registries.COMMAND_ARGUMENT_TYPE,
                "battlegrounds:pvp_mode",
                PVPModeArgumentType.class,
                ConstantArgumentSerializer.of(PVPModeArgumentType::pvpMode)
        );
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Constants.LOGGER.info("Loading {}", Constants.MOD_NAME);
        // 加载配置
        this.loadConfigs();
        // 注册事件
        // 指令
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        ServerLifecycleEvents.AFTER_SAVE.register(this::onSaving);
        LootTableEvents.MODIFY.register(new LootTableModifier());
        // 注册网络包相关
        ModServerConfigurationNetworkHandler.register();
        ModServerPlayNetworkHandler.register();
        // 注册数据包
        ResourceManagerHelper resourceManagerHelper = ResourceManagerHelper.get(ResourceType.SERVER_DATA);
        resourceManagerHelper.registerReloadListener(new DataPackResourceReloadListener());
        // 注册物品
        Items.bootstrap();
        // 注册物品组
        ItemGroups.bootstrap();
        // 注册战利品表物品修饰器
        LootFunctionTypes.bootstrap();
        // 注册实体
        EntityTypes.bootstrap();
        // 注册状态效果
        StatusEffects.bootstrap();
        // 注册附魔
        Enchantments.bootstrap();
        // 自动冶炼
        SmeltableBlockRegistry.registerDefaults();
        // 注册声音事件
        SoundEvents.bootstrap();
        // 注册指令参数类型
        this.registerCommandArgumentTypes();
        // 尝试重置存档
        this.tryDeleteWorld();
    }
}
