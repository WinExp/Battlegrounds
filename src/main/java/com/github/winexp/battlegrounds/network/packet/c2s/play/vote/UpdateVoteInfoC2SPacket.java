package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UpdateVoteInfoC2SPacket(Identifier identifier) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/update_vote_info");
    public static final PacketType<UpdateVoteInfoC2SPacket> TYPE = PacketType.create(ID, UpdateVoteInfoC2SPacket::new);

    public UpdateVoteInfoC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.identifier);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
