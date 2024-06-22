package com.github.winexp.battlegrounds;

import com.github.winexp.battlegrounds.block.Blocks;
import com.github.winexp.battlegrounds.block.SmeltableBlockRegistry;
import com.github.winexp.battlegrounds.block.entity.BlockEntityType;
import com.github.winexp.battlegrounds.command.BattlegroundsCommand;
import com.github.winexp.battlegrounds.command.VoteCommand;
import com.github.winexp.battlegrounds.command.argument.VoteArgumentType;
import com.github.winexp.battlegrounds.component.DataComponentTypes;
import com.github.winexp.battlegrounds.config.RootConfig;
import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.ItemGroups;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.LootTableModifier;
import com.github.winexp.battlegrounds.loot.function.LootFunctionTypes;
import com.github.winexp.battlegrounds.mixin.ArgumentTypesInvoker;
import com.github.winexp.battlegrounds.network.ModServerConfigurationNetworkHandler;
import com.github.winexp.battlegrounds.network.ModServerPlayNetworkHandler;
import com.github.winexp.battlegrounds.network.payload.c2s.config.ModVersionPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.RupertsTearTeleportPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.QueryVotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.SyncVotesPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.VotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.config.ModVersionPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.FlashPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.config.ModGameConfigPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.*;
import com.github.winexp.battlegrounds.registry.ModRegistries;
import com.github.winexp.battlegrounds.screen.ScreenHandlerType;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.task.TaskScheduler;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.Variables;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;

public class Battlegrounds implements ModInitializer {
    public static Battlegrounds INSTANCE;

    public static void loadConfigs() {
        RootConfig.HANDLER.load();
    }

    public void reload() {
        loadConfigs();
        Items.addRecipes();
    }

    private void onServerStarting(MinecraftServer server) {
        Variables.server = server;
        loadConfigs();
    }

    private void onServerStopping(MinecraftServer server) {
        TaskScheduler.INSTANCE.cancelAllTasks();
    }

    private void registerPayloads() {
        PayloadTypeRegistry.configurationS2C().register(ModVersionPayloadS2C.ID, ModVersionPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ModGameConfigPayloadS2C.ID, ModGameConfigPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(FlashPayloadS2C.ID, FlashPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(VoteOpenedPayloadS2C.ID, VoteOpenedPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(VoteClosedPayloadS2C.ID, VoteClosedPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(VoteUpdatedPayloadS2C.ID, VoteUpdatedPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncVotesPayloadS2C.ID, SyncVotesPayloadS2C.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerVotedPayloadS2C.ID, PlayerVotedPayloadS2C.PACKET_CODEC);

        PayloadTypeRegistry.configurationC2S().register(ModVersionPayloadC2S.ID, ModVersionPayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RupertsTearTeleportPayloadC2S.ID, RupertsTearTeleportPayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(VotePayloadC2S.ID, VotePayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(QueryVotePayloadC2S.ID, QueryVotePayloadC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncVotesPayloadC2S.ID, SyncVotesPayloadC2S.PACKET_CODEC);
    }

    private void registerArgumentTypes() {
        ArgumentTypesInvoker.invokeRegister(Registries.COMMAND_ARGUMENT_TYPE, "battlegrounds:vote", VoteArgumentType.class, ConstantArgumentSerializer.of(VoteArgumentType::vote));
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        Constants.LOGGER.info("Loading {}", Constants.MOD_NAME);
        // 加载配置
        loadConfigs();
        // 注册事件
        // 指令
        CommandRegistrationCallback.EVENT.register(new VoteCommand());
        CommandRegistrationCallback.EVENT.register(new BattlegroundsCommand());
        // Fabric API
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        LootTableEvents.MODIFY.register(new LootTableModifier());
        // 注册网络包相关
        this.registerPayloads();
        ModServerConfigurationNetworkHandler.register();
        ModServerPlayNetworkHandler.register();
        // 注册注册表
        ModRegistries.bootstrap();
        // 注册屏幕处理器
        ScreenHandlerType.bootstrap();
        // 注册方块
        Blocks.bootstrap();
        // 注册方块实体
        BlockEntityType.bootstrap();
        // 注册物品堆叠组件
        DataComponentTypes.bootstrap();
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
        // 注册自动冶炼方块
        SmeltableBlockRegistry.registerDefaults();
        // 注册声音事件
        SoundEvents.bootstrap();
        // 注册命令参数类型
        this.registerArgumentTypes();
    }
}
