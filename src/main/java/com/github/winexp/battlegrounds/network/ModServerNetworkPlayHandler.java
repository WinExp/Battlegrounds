package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.network.packet.c2s.VoteC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.SyncVoteInfoC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModServerNetworkPlayHandler {
    public static void onGetVoteInfos(SyncVoteInfoC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        VoteManager.INSTANCE.updateVoteInfos(player, false);
    }

    public static void onVote(VoteC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        Identifier identifier = packet.identifier();
        if (VoteManager.INSTANCE.isVoting(identifier)) {
            VoteInstance instance = VoteManager.INSTANCE.getVoteInstance(identifier);
            if (packet.result()) {
                instance.acceptVote(player);
            } else {
                instance.denyVote(player);
            }
        }
    }
}
