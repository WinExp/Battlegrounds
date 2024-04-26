package com.github.winexp.battlegrounds.network.packet.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VotePayloadC2S(Identifier identifier, boolean result) implements CustomPayload {
    public static final Id<VotePayloadC2S> ID = CustomPayload.id("battlegrounds:play/vote/vote");
    public static final PacketCodec<PacketByteBuf, VotePayloadC2S> PACKET_CODEC = CustomPayload.codecOf(VotePayloadC2S::write, VotePayloadC2S::new);

    public VotePayloadC2S(PacketByteBuf buf) {
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
