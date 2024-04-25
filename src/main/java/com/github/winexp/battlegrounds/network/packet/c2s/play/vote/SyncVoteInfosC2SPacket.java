package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncVoteInfosC2SPacket() implements CustomPayload {
    public static final Id<SyncVoteInfosC2SPacket> ID = CustomPayload.id("battlegrounds:play/vote/sync_vote_infos");
    public static final PacketCodec<PacketByteBuf, SyncVoteInfosC2SPacket> PACKET_CODEC = CustomPayload.codecOf(SyncVoteInfosC2SPacket::write, SyncVoteInfosC2SPacket::new);

    public SyncVoteInfosC2SPacket(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
