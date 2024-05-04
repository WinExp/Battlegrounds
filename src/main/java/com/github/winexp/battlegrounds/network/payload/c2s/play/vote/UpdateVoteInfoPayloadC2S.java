package com.github.winexp.battlegrounds.network.payload.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateVoteInfoPayloadC2S(Identifier voteId) implements CustomPayload {
    public static final Id<UpdateVoteInfoPayloadC2S> ID = CustomPayload.id("battlegrounds:play/vote/get_vote_info");
    public static final PacketCodec<PacketByteBuf, UpdateVoteInfoPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(UpdateVoteInfoPayloadC2S::write, UpdateVoteInfoPayloadC2S::new);

    public UpdateVoteInfoPayloadC2S(PacketByteBuf buf) {
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
