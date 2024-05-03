package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.network.packet.c2s.config.ModVersionC2SPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.config.ModVersionS2CPacket;
import com.github.winexp.battlegrounds.util.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

@Environment(EnvType.CLIENT)
public final class ModClientConfigurationNetworkHandler {
    public static void register() {
        ClientConfigurationNetworking.registerGlobalReceiver(ModVersionS2CPacket.TYPE, ModClientConfigurationNetworkHandler::onModVersionReceived);
    }

    private static void onModVersionReceived(ModVersionS2CPacket packet, PacketSender sender) {
        ClientConfigurationNetworking.send(new ModVersionC2SPacket(Constants.MOD_VERSION));
    }
}
