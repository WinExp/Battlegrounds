package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.gui.screen.SoakScreen;
import com.github.winexp.battlegrounds.client.network.ModClientConfigurationNetworkHandler;
import com.github.winexp.battlegrounds.client.network.ModClientPlayNetworkHandler;
import com.github.winexp.battlegrounds.client.render.entity.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.screen.ScreenHandlerType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

@Environment(EnvType.CLIENT)
public class BattlegroundsClient implements ClientModInitializer {
    private static void registerRenderer() {
        // 实体渲染器
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.MOLOTOV, FlyingItemEntityRenderer::new);

        // 自定义渲染器
        HudRenderCallback.EVENT.register(ClientConstants.FLASH_RENDERER);
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(ClientConstants.RUPERTS_TEAR_BLOCK_OUTLINE_RENDERER);
    }

    private static void registerHandledScreen() {
        HandledScreens.register(ScreenHandlerType.SOAK, SoakScreen::new);
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        registerRenderer();
        registerHandledScreen();
        // 注册网络包相关
        ModClientConfigurationNetworkHandler.register();
        ModClientPlayNetworkHandler.register();
        // 注册按键绑定
        KeyBindings.bootstrap();
        // 注册物品模型谓词
        Items.registerModelPredicates();
    }
}
