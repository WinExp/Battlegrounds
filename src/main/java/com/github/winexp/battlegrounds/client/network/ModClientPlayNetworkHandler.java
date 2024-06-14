package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.network.payload.s2c.play.FlashPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.config.ModGameConfigPayloadS2C;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register(ModClientPlayNetworkHandler::onDisconnect);
        ClientPlayNetworking.registerGlobalReceiver(ModGameConfigPayloadS2C.ID, ModClientPlayNetworkHandler::onModGameConfigReceived);
        ClientPlayNetworking.registerGlobalReceiver(FlashPayloadS2C.ID, ModClientPlayNetworkHandler::onFlash);
    }

    private static void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientVariables.resetGameConfig();
    }

    private static void onModGameConfigReceived(ModGameConfigPayloadS2C packet, ClientPlayNetworking.Context context) {
        ClientVariables.gameConfig = packet.config();
    }

    private static void onFlash(FlashPayloadS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        Vec3d pos = packet.pos();
        float distance = packet.distance();
        Entity entity = client.getCameraEntity();
        if (entity != null) {
            float tickDelta = client.getTickDelta();
            float strength = FlashRenderer.calculateFlashStrength(entity, pos, distance, tickDelta);
            if (client.player != null && client.player.isSpectator()) {
                strength = 0.6F;
            }
            ClientConstants.FLASH_RENDERER.setFlashStrength(strength);
        }
    }
}
