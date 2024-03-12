package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.network.ModClientNetworkPlayHandler;
import com.github.winexp.battlegrounds.client.render.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class BattlegroundsClient implements ClientModInitializer {

    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
    }

    private void tick(MinecraftClient client) {
        // 闪光弹
        if (ClientVariables.flashStrength > 0) {
            ClientVariables.flashStrength -= FlashBangEntity.STRENGTH_LEFT_SPEED;
        } else if (ClientVariables.flashStrength < 0){
            ClientVariables.flashStrength = 0;
        }

        // 按键绑定
        while (KeyBindings.VOTE_SCREEN.wasPressed()) {
            client.setScreen(new VoteScreen());
        }
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包接收器
        ModClientNetworkPlayHandler.registerReceivers();
        // 注册按键绑定
        KeyBindings.registerKeyBindings();
        // 注册 tick
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
}
