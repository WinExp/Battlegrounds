package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record VoteOpenedPayloadS2C(VoteInstance voteInstance) implements CustomPayload {
    public static final Id<VoteOpenedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/vote_opened");
    public static final PacketCodec<PacketByteBuf, VoteOpenedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(VoteOpenedPayloadS2C::write, VoteOpenedPayloadS2C::new);

    public VoteOpenedPayloadS2C(PacketByteBuf buf) {
        this(VoteInstance.PACKET_CODEC.decode(buf));
    }

    public void write(PacketByteBuf buf) {
        VoteInstance.PACKET_CODEC.encode(buf, this.voteInstance);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
