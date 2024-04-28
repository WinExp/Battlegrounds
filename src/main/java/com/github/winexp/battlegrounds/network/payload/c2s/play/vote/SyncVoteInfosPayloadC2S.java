package com.github.winexp.battlegrounds.network.payload.c2s.play.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SyncVoteInfosPayloadC2S() implements CustomPayload {
    public static final Id<SyncVoteInfosPayloadC2S> ID = CustomPayload.id("battlegrounds:play/vote/sync_vote_infos");
    public static final PacketCodec<PacketByteBuf, SyncVoteInfosPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(SyncVoteInfosPayloadC2S::write, SyncVoteInfosPayloadC2S::new);

    public SyncVoteInfosPayloadC2S(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
