package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record UpdateVoteInfoS2CPacket(Optional<VoteInfo> voteInfo) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/update_vote_info");
    public static final PacketType<UpdateVoteInfoS2CPacket> TYPE = PacketType.create(ID, UpdateVoteInfoS2CPacket::new);

    public UpdateVoteInfoS2CPacket(PacketByteBuf buf) {
        this(buf.readOptional(VoteInfo.PACKET_READER));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeOptional(this.voteInfo, VoteInfo.PACKET_WRITER);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
