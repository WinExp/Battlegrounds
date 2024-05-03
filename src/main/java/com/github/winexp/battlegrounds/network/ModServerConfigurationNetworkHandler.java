package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.network.packet.c2s.config.ModVersionC2SPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.config.ModVersionS2CPacket;
import com.github.winexp.battlegrounds.network.task.s2c.config.ModVersionConfigurationTask;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ModServerConfigurationNetworkHandler {
    public static void register() {
        ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.register(ModServerConfigurationNetworkHandler::onBeforeConfigure);
        ServerConfigurationConnectionEvents.CONFIGURE.register(ModServerConfigurationNetworkHandler::onConfigure);
        ServerConfigurationNetworking.registerGlobalReceiver(ModVersionC2SPacket.TYPE, ModServerConfigurationNetworkHandler::onModInfoReceived);
    }

    private static void onBeforeConfigure(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        PlayerUtil.setPlayerModVersion(handler.getProfile().getId(), null);
    }

    private static void onConfigure(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        if (ServerConfigurationNetworking.canSend(handler, ModVersionS2CPacket.TYPE)) {
            handler.addTask(new ModVersionConfigurationTask(handler));
        } else {
            handler.disconnect(Text.translatable(
                    "disconnect.battlegrounds.config.mod_info.not_found",
                    Constants.MOD_VERSION.version().getFriendlyString(),
                    Constants.MOD_VERSION.protocolVersion()
            ).formatted(Formatting.RED).styled(style -> style.withBold(true)));
        }
    }

    private static void onModInfoReceived(ModVersionC2SPacket packet, ServerConfigurationNetworkHandler handler, PacketSender sender) {
        ModVersion playerModVersion = packet.modVersion();
        if (playerModVersion.protocolVersion() != Constants.MOD_VERSION.protocolVersion()) {
            handler.disconnect(Text.translatable(
                            "disconnect.battlegrounds.config.mod_info.failed",
                            playerModVersion.version().getFriendlyString(),
                            playerModVersion.protocolVersion(),
                            Constants.MOD_VERSION.version().getFriendlyString(),
                            Constants.MOD_VERSION.protocolVersion()
                    )
                    .formatted(Formatting.RED)
                    .styled(style -> style.withBold(true)));
            return;
        }
        PlayerUtil.setPlayerModVersion(handler.getDebugProfile().getId(), packet.modVersion());
        handler.completeTask(ModVersionConfigurationTask.KEY);
    }
}
