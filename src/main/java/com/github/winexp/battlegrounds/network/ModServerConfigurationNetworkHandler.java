package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.network.packet.c2s.config.ModInfoC2SPacket;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;

public final class ModServerConfigurationNetworkHandler {
    public static void register() {
        ServerConfigurationNetworking.registerGlobalReceiver(ModInfoC2SPacket.TYPE, ModServerConfigurationNetworkHandler::onModInfoReceived);
        ServerConfigurationConnectionEvents.DISCONNECT.register(ModServerConfigurationNetworkHandler::onConfigureDisconnect);
    }

    private static void onModInfoReceived(ModInfoC2SPacket packet, ServerConfigurationNetworkHandler handler, PacketSender sender) {
        PlayerUtil.setPlayerModVersion(handler.getDebugProfile().getId(), packet.modVersion());
    }

    private static void onConfigureDisconnect(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        PlayerUtil.setPlayerModVersion(handler.getProfile().getId(), null);
    }
}
