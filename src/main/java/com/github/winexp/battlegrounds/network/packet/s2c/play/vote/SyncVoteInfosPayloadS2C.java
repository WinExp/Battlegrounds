package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Collection;

public record SyncVoteInfosPayloadS2C(Collection<VoteInfo> voteInfos) implements CustomPayload {
    public static final Id<SyncVoteInfosPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/sync_vote_infos");
    public static final PacketCodec<PacketByteBuf, SyncVoteInfosPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(SyncVoteInfosPayloadS2C::write, SyncVoteInfosPayloadS2C::new);

    public SyncVoteInfosPayloadS2C(PacketByteBuf buf) {
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
