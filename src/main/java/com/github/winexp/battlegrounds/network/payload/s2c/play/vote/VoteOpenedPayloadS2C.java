package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record VoteOpenedPayloadS2C(UUID uuid) implements CustomPayload {
    public static final Id<VoteOpenedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/opened");
    public static final PacketCodec<PacketByteBuf, VoteOpenedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(VoteOpenedPayloadS2C::write, VoteOpenedPayloadS2C::new);

    public VoteOpenedPayloadS2C(PacketByteBuf buf) {
        this(buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
