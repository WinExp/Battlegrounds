package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.network.payload.c2s.config.ModInfoPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.config.ModInfoPayloadS2C;
import com.github.winexp.battlegrounds.network.task.s2c.config.ModVersionConfigurationTask;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.data.ModVersion;
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
        ServerConfigurationNetworking.registerGlobalReceiver(ModInfoPayloadC2S.ID, ModServerConfigurationNetworkHandler::onModInfoReceived);
    }

    private static void onBeforeConfigure(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        PlayerUtil.setPlayerModVersion(handler.getDebugProfile().getId(), null);
    }

    private static void onConfigure(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
        if (ServerConfigurationNetworking.canSend(handler, ModInfoPayloadS2C.ID)) {
            handler.addTask(new ModVersionConfigurationTask(handler));
        } else {
            handler.disconnect(Text.translatable(
                    "disconnect.battlegrounds.config.mod_info.not_found",
                    Constants.MOD_VERSION
            ).formatted(Formatting.RED).styled(style -> style.withBold(true)));
        }
    }

    private static void onModInfoReceived(ModInfoPayloadC2S packet, ServerConfigurationNetworking.Context context) {
        ModVersion playerModVersion = packet.modVersion();
        ServerConfigurationNetworkHandler handler = context.networkHandler();
        if (playerModVersion.protocolVersion() != Constants.MOD_VERSION.protocolVersion()) {
            handler.disconnect(Text.translatable(
                            "disconnect.battlegrounds.config.mod_info.wrong",
                            playerModVersion,
                            Constants.MOD_VERSION
                    )
                    .formatted(Formatting.RED)
                    .styled(style -> style.withBold(true)));
        }
        PlayerUtil.setPlayerModVersion(handler.getDebugProfile().getId(), playerModVersion);
        handler.completeTask(ModVersionConfigurationTask.KEY);
    }
}
