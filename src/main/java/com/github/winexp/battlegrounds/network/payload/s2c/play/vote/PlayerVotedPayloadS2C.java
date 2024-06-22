package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.UUID;

public record PlayerVotedPayloadS2C(UUID uuid, Text playerName, boolean result) implements CustomPayload {
    public static final Id<PlayerVotedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/voted");
    public static final PacketCodec<PacketByteBuf, PlayerVotedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(PlayerVotedPayloadS2C::write, PlayerVotedPayloadS2C::new);

    public PlayerVotedPayloadS2C(PacketByteBuf buf) {
        this(buf.readUuid(), TextCodecs.PACKET_CODEC.decode(buf), buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
        TextCodecs.PACKET_CODEC.encode(buf, this.playerName);
        buf.writeBoolean(this.result);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
