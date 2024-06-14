package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.network.payload.c2s.config.ModVersionPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.config.ModVersionPayloadS2C;
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
        ClientConfigurationNetworking.registerGlobalReceiver(ModVersionPayloadS2C.ID, ModClientConfigurationNetworkHandler::onModInfoReceived);
    }

    private static void onModInfoReceived(ModVersionPayloadS2C payload, ClientConfigurationNetworking.Context context) {
        PacketSender sender = context.responseSender();
        ModVersion serverModVersion = payload.modVersion();
        if (Constants.MOD_VERSION.protocolVersion() != serverModVersion.protocolVersion()) {
            sender.disconnect(Text.stringifiedTranslatable(
                            "disconnect.battlegrounds.config.mod_info.wrong",
                            Constants.MOD_VERSION,
                            serverModVersion
                    )
                    .formatted(Formatting.RED)
                    .styled(style -> style.withBold(true)));
            return;
        }
        sender.sendPacket(new ModVersionPayloadC2S(Constants.MOD_VERSION));
    }
}
