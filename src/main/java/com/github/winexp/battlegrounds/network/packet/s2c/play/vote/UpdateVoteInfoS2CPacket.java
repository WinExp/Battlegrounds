package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;

public record UpdateVoteInfoS2CPacket(Optional<VoteInfo> voteInfo) implements CustomPayload {
    public static final Id<UpdateVoteInfoS2CPacket> ID = CustomPayload.id("battlegrounds:play/vote/update_vote_info");
    public static final PacketCodec<PacketByteBuf, UpdateVoteInfoS2CPacket> PACKET_CODEC = CustomPayload.codecOf(UpdateVoteInfoS2CPacket::write, UpdateVoteInfoS2CPacket::new);

    public UpdateVoteInfoS2CPacket(PacketByteBuf buf) {
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
