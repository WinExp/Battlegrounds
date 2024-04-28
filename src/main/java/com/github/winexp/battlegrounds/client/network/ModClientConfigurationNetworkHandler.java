package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.network.payload.c2s.config.ModInfoPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.config.ModInfoPayloadS2C;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public final class ModClientConfigurationNetworkHandler {
    public static void register() {
        ClientConfigurationNetworking.registerGlobalReceiver(ModInfoPayloadS2C.ID, ModClientConfigurationNetworkHandler::onModInfoReceived);
    }

    private static void onModInfoReceived(ModInfoPayloadS2C payload, ClientConfigurationNetworking.Context context) {
        PacketSender sender = context.responseSender();
        ModVersion serverModVersion = payload.modVersion();
        if (Constants.MOD_VERSION.protocolVersion() != serverModVersion.protocolVersion()) {
            sender.disconnect(Text.translatable(
                            "disconnect.battlegrounds.config.mod_info.wrong",
                            Constants.MOD_VERSION,
                            serverModVersion
                    )
                    .formatted(Formatting.RED)
                    .styled(style -> style.withBold(true)));
            return;
        }
        sender.sendPacket(new ModInfoPayloadC2S(Constants.MOD_VERSION));
    }
}
