package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record VoteOpenedS2CPacket(VoteInfo voteInfo) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/vote_opened");
    public static final PacketType<VoteOpenedS2CPacket> TYPE = PacketType.create(ID, VoteOpenedS2CPacket::new);

    public VoteOpenedS2CPacket(PacketByteBuf buf) {
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
