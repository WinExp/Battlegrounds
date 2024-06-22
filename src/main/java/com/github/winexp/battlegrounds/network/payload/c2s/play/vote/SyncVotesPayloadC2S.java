package com.github.winexp.battlegrounds.network.payload.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncVotesPayloadC2S() implements CustomPayload {
    public static final Id<SyncVotesPayloadC2S> ID = CustomPayload.id("battlegrounds:play/vote/sync");
    public static final PacketCodec<PacketByteBuf, SyncVotesPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(SyncVotesPayloadC2S::write, SyncVotesPayloadC2S::new);

    public SyncVotesPayloadC2S(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
