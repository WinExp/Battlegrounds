package com.github.winexp.battlegrounds.network.packet.s2c;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public record VoteInfosS2CPacket(Collection<VoteInfo> voteInfos) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "return_vote_infos");
    public static final PacketType<VoteInfosS2CPacket> TYPE = PacketType.create(ID, VoteInfosS2CPacket::new);

    public VoteInfosS2CPacket(PacketByteBuf buf) {
        this(new ArrayList<>());
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            this.voteInfos.add(VoteInfo.readVoteInfoFromBuf(buf));
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.voteInfos.size());
        for (VoteInfo voteInfo : this.voteInfos) {
            VoteInfo.writeVoteInfoToBuf(buf, voteInfo);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
