package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.Vote;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;
import java.util.UUID;

public record VoteUpdatedPayloadS2C(UUID uuid, Optional<Vote> vote) implements CustomPayload {
    public static final Id<VoteUpdatedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/updated");
    public static final PacketCodec<RegistryByteBuf, VoteUpdatedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(VoteUpdatedPayloadS2C::write, VoteUpdatedPayloadS2C::new);
    private static final PacketCodec<RegistryByteBuf, Optional<Vote>> OPTIONAL_VOTE_PACKET_CODEC = PacketCodecs.optional(Vote.PACKET_CODEC);

    public VoteUpdatedPayloadS2C(RegistryByteBuf buf) {
        this(buf.readUuid(), OPTIONAL_VOTE_PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeUuid(this.uuid);
        OPTIONAL_VOTE_PACKET_CODEC.encode(buf, this.vote);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
