package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.Vote;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Collection;

public record SyncVotesPayloadS2C(Collection<Vote> votes) implements CustomPayload {
    public static final Id<SyncVotesPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/sync");
    public static final PacketCodec<RegistryByteBuf, SyncVotesPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(SyncVotesPayloadS2C::write, SyncVotesPayloadS2C::new);
    private static final PacketCodec<RegistryByteBuf, Collection<Vote>> VOTE_LIST_PACKET_CODEC = PacketCodecs.collection(ObjectOpenHashSet::new, Vote.PACKET_CODEC);

    public SyncVotesPayloadS2C(RegistryByteBuf buf) {
        this(VOTE_LIST_PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        VOTE_LIST_PACKET_CODEC.encode(buf, this.votes);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
