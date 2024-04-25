package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GetVoteInfoC2SPacket(Identifier voteId) implements CustomPayload {
    public static final Id<GetVoteInfoC2SPacket> ID = CustomPayload.id("battlegrounds:play/vote/get_vote_info");
    public static final PacketCodec<PacketByteBuf, GetVoteInfoC2SPacket> PACKET_CODEC = CustomPayload.codecOf(GetVoteInfoC2SPacket::write, GetVoteInfoC2SPacket::new);

    public GetVoteInfoC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier());
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.voteId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
