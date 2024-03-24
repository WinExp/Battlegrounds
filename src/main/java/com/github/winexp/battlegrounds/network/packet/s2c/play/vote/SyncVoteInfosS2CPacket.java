package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Collection;

public record SyncVoteInfosS2CPacket(Collection<VoteInfo> voteInfos) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/sync_vote_infos");
    public static final PacketType<SyncVoteInfosS2CPacket> TYPE = PacketType.create(ID, SyncVoteInfosS2CPacket::new);

    public SyncVoteInfosS2CPacket(PacketByteBuf buf) {
        this(buf.readList(VoteInfo.PACKET_READER));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.voteInfos, VoteInfo.PACKET_WRITER);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
