package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.network.ModClientNetworkPlayHandler;
import com.github.winexp.battlegrounds.client.render.entity.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class BattlegroundsClient implements ClientModInitializer {

    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.MOLOTOV, FlyingItemEntityRenderer::new);
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包接收器
        ModClientNetworkPlayHandler.registerReceivers();
        // 注册按键绑定
        KeyBindings.registerKeyBindings();
        // 注册物品模型谓词
        Items.registerModelPredicates();
    }
}
