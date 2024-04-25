package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Collection;

public record SyncVoteInfosS2CPacket(Collection<VoteInfo> voteInfos) implements CustomPayload {
    public static final Id<SyncVoteInfosS2CPacket> ID = CustomPayload.id("battlegrounds:play/vote/sync_vote_infos");
    public static final PacketCodec<PacketByteBuf, SyncVoteInfosS2CPacket> PACKET_CODEC = CustomPayload.codecOf(SyncVoteInfosS2CPacket::write, SyncVoteInfosS2CPacket::new);

    public SyncVoteInfosS2CPacket(PacketByteBuf buf) {
        this(buf.readList(VoteInfo.PACKET_CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.voteInfos, VoteInfo.PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
