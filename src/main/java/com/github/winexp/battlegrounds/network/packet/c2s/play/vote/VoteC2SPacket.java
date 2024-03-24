package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record VoteC2SPacket(Identifier identifier, boolean result) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/vote");
    public static final PacketType<VoteC2SPacket> TYPE = PacketType.create(ID, VoteC2SPacket::new);

    public VoteC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.identifier);
        buf.writeBoolean(this.result);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
