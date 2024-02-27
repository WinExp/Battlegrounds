package com.github.winexp.battlegrounds.client;

import com.github.winexp.battlegrounds.client.render.ChannelingArrowEntityRenderer;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class BattlegroundsClient implements ClientModInitializer {
    private void registerRenderer() {
        EntityRendererRegistry.register(EntityTypes.CHANNELING_ARROW, ChannelingArrowEntityRenderer::new);
        EntityRendererRegistry.register(EntityTypes.FLASH_BANG, FlyingItemEntityRenderer::new);
    }

    private void tryFlash(MinecraftClient client, Vec3d flashPos, float distance) {
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                float tickDelta = client.getTickDelta();
                ClientVariables.INSTANCE.flashStrength = Math.max(ClientVariables.INSTANCE.flashStrength,
                        FlashBangEntity.getFlashStrength(entity, tickDelta, flashPos, distance));
            }
        });
    }

    private void registerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.FLASH_BANG_PACKET_ID, (client, handler, buf, responseSender) -> {
            float distance = buf.readFloat();
            Vec3d flashPos = buf.readVec3d();
            this.tryFlash(client, flashPos, distance);
        });
    }

    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        this.registerRenderer();
        // 注册网络包接收器
        this.registerReceiver();
    }
}
