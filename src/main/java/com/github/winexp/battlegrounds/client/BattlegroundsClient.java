package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.renderer.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class BattlegroundsClient implements ClientModInitializer {
    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        registerRenderer();
    }
}
