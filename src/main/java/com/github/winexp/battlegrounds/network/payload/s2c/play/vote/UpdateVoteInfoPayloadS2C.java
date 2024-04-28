package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;

public record UpdateVoteInfoPayloadS2C(Optional<VoteInfo> voteInfo) implements CustomPayload {
    public static final Id<UpdateVoteInfoPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/update_vote_info");
    public static final PacketCodec<PacketByteBuf, UpdateVoteInfoPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(UpdateVoteInfoPayloadS2C::write, UpdateVoteInfoPayloadS2C::new);

    public UpdateVoteInfoPayloadS2C(PacketByteBuf buf) {
        this(buf.readOptional(VoteInfo.PACKET_CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeOptional(this.voteInfo, VoteInfo.PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
