package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record VoteClosedS2CPacket(VoteInfo voteInfo, VoteSettings.CloseReason closeReason) implements CustomPayload {
    public static final Id<VoteClosedS2CPacket> ID = CustomPayload.id("battlegrounds:play/vote/vote_closed");
    public static final PacketCodec<PacketByteBuf, VoteClosedS2CPacket> PACKET_CODEC = CustomPayload.codecOf(VoteClosedS2CPacket::write, VoteClosedS2CPacket::new);

    public VoteClosedS2CPacket(PacketByteBuf buf) {
        this(VoteInfo.PACKET_CODEC.decode(buf), buf.readEnumConstant(VoteSettings.CloseReason.class));
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
