package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record SyncVoteInfosPayloadS2C(List<VoteInstance> voteInstances) implements CustomPayload {
    public static final Id<SyncVoteInfosPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/sync_vote_infos");
    public static final PacketCodec<PacketByteBuf, SyncVoteInfosPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(SyncVoteInfosPayloadS2C::write, SyncVoteInfosPayloadS2C::new);

    public SyncVoteInfosPayloadS2C(PacketByteBuf buf) {
        this(buf.readList(VoteInstance.PACKET_CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.voteInstances, VoteInstance.PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
