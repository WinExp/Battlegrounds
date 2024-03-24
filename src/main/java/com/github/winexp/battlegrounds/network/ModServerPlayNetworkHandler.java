package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.VoteC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.SyncVoteInfosC2SPacket;
import com.github.winexp.battlegrounds.util.Constants;
import com.github.winexp.battlegrounds.util.ModVersion;
import com.github.winexp.battlegrounds.util.PlayerUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ModServerPlayNetworkHandler {
    public static void register() {
        ServerPlayConnectionEvents.INIT.register(ModServerPlayNetworkHandler::onPlayInit);
        ServerPlayConnectionEvents.DISCONNECT.register(ModServerPlayNetworkHandler::onPlayDisconnect);
        ServerPlayNetworking.registerGlobalReceiver(SyncVoteInfosC2SPacket.TYPE, ModServerPlayNetworkHandler::onSyncVoteInfos);
        ServerPlayNetworking.registerGlobalReceiver(VoteC2SPacket.TYPE, ModServerPlayNetworkHandler::onVote);
    }

    private static void onPlayInit(ServerPlayNetworkHandler handler, MinecraftServer server) {
        UUID uuid = handler.getDebugProfile().getId();
        ModVersion playerModVersion = PlayerUtil.getPlayerModVersion(uuid);
        if (playerModVersion == null) {
            handler.disconnect(Text.translatable(
                    "battlegrounds.network.config.mod_info.not_found",
                    Constants.MOD_VERSION.version().getFriendlyString(),
                    Constants.MOD_VERSION.protocolVersion()
            ).formatted(Formatting.RED).styled(style -> style.withBold(true)));
        } else if (playerModVersion.protocolVersion() != Constants.MOD_VERSION.protocolVersion()) {
            handler.disconnect(Text.translatable(
                            "battlegrounds.network.config.mod_info.failed",
                            playerModVersion.version().getFriendlyString(),
                            playerModVersion.protocolVersion(),
                            Constants.MOD_VERSION.version().getFriendlyString(),
                            Constants.MOD_VERSION.protocolVersion()
                    )
                    .formatted(Formatting.RED)
                    .styled(style -> style.withBold(true)));
        }
    }

    private static void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        PlayerUtil.setPlayerModVersion(handler.getDebugProfile().getId(), null);
    }

    private static void onSyncVoteInfos(SyncVoteInfosC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        player.server.execute(() -> VoteManager.INSTANCE.syncVoteInfos(player));
    }

    private static void onVote(VoteC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        Identifier identifier = packet.identifier();
        if (VoteManager.INSTANCE.isVoting(identifier)) {
            VoteInstance instance = VoteManager.INSTANCE.getVoteInstance(identifier).orElseThrow();
            if (packet.result()) {
                instance.acceptVote(player);
            } else {
                instance.denyVote(player);
            }
        }
    }
}
