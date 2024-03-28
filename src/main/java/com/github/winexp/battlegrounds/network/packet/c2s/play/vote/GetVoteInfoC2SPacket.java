package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record GetVoteInfoC2SPacket(Identifier voteId) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/get_vote_info");
    public static final PacketType<GetVoteInfoC2SPacket> TYPE = PacketType.create(ID, GetVoteInfoC2SPacket::new);

    public GetVoteInfoC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.voteId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
