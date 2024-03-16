package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.network.packet.c2s.VoteC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.SyncVoteInfosC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModServerNetworkPlayHandler {
    public static void registerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SyncVoteInfosC2SPacket.TYPE, ModServerNetworkPlayHandler::onGetVoteInfos);
        ServerPlayNetworking.registerGlobalReceiver(VoteC2SPacket.TYPE, ModServerNetworkPlayHandler::onVote);
    }

    private static void onGetVoteInfos(SyncVoteInfosC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        VoteManager.INSTANCE.syncVoteInfos(player);
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
