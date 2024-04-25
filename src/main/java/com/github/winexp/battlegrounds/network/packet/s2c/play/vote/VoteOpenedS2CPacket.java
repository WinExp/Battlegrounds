package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record VoteOpenedS2CPacket(VoteInfo voteInfo) implements CustomPayload {
    public static final Id<VoteOpenedS2CPacket> ID = CustomPayload.id("battlegrounds:play/vote/vote_opened");
    public static final PacketCodec<PacketByteBuf, VoteOpenedS2CPacket> PACKET_CODEC = CustomPayload.codecOf(VoteOpenedS2CPacket::write, VoteOpenedS2CPacket::new);

    public VoteOpenedS2CPacket(PacketByteBuf buf) {
        this(VoteInfo.PACKET_CODEC.decode(buf));
    }

    public void write(PacketByteBuf buf) {
        VoteInfo.PACKET_CODEC.encode(buf, this.voteInfo);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
