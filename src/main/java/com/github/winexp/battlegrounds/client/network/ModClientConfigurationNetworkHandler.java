package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.network.packet.c2s.config.ModInfoC2SPacket;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;

@Environment(EnvType.CLIENT)
public class ModClientConfigurationNetworkHandler {
    public static void register() {
        ClientConfigurationConnectionEvents.INIT.register(ModClientConfigurationNetworkHandler::onConfigurationInit);
    }

    private static void onConfigurationInit(ClientConfigurationNetworkHandler handler, MinecraftClient client) {
        ClientConfigurationNetworking.send(new ModInfoC2SPacket(Constants.MOD_VERSION));
    }
}
