package com.github.winexp.battlegrounds.network.packet.s2c;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record VoteOpenedPacket(VoteInfo voteInfo) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "vote_opened");
    public static final PacketType<VoteOpenedPacket> TYPE = PacketType.create(ID, VoteOpenedPacket::new);

    public VoteOpenedPacket(PacketByteBuf buf) {
        this(VoteInfo.PACKET_READER.apply(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        VoteInfo.PACKET_WRITER.accept(buf, this.voteInfo);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
