package com.github.winexp.battlegrounds.network.payload.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record QueryVotePayloadC2S(UUID uuid) implements CustomPayload {
    public static final Id<QueryVotePayloadC2S> ID = CustomPayload.id("battlegrounds:play/vote/query");
    public static final PacketCodec<PacketByteBuf, QueryVotePayloadC2S> PACKET_CODEC = CustomPayload.codecOf(QueryVotePayloadC2S::write, QueryVotePayloadC2S::new);

    public QueryVotePayloadC2S(PacketByteBuf buf) {
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
