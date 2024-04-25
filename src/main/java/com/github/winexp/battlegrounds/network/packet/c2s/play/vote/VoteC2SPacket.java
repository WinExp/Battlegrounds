package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VoteC2SPacket(Identifier identifier, boolean result) implements CustomPayload {
    public static final Id<VoteC2SPacket> ID = CustomPayload.id("battlegrounds:play/vote/vote");
    public static final PacketCodec<PacketByteBuf, VoteC2SPacket> PACKET_CODEC = CustomPayload.codecOf(VoteC2SPacket::write, VoteC2SPacket::new);

    public VoteC2SPacket(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.identifier);
        buf.writeBoolean(this.result);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
