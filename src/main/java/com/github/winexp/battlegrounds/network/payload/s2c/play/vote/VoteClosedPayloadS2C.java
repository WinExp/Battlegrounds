package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.CloseReason;
import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record VoteClosedPayloadS2C(VoteInfo voteInfo, CloseReason closeReason) implements CustomPayload {
    public static final Id<VoteClosedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/vote_closed");
    public static final PacketCodec<PacketByteBuf, VoteClosedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(VoteClosedPayloadS2C::write, VoteClosedPayloadS2C::new);

    public VoteClosedPayloadS2C(PacketByteBuf buf) {
        this(VoteInfo.PACKET_CODEC.decode(buf), buf.readEnumConstant(CloseReason.class));
    }

    public void write(PacketByteBuf buf) {
        VoteInfo.PACKET_CODEC.encode(buf, this.voteInfo);
        buf.writeEnumConstant(closeReason);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
