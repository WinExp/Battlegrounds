package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record UpdateVoteInfoPayloadS2C(Identifier identifier, Optional<VoteInstance> voteInstance) implements CustomPayload {
    public static final Id<UpdateVoteInfoPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/update_vote_info");
    public static final PacketCodec<PacketByteBuf, UpdateVoteInfoPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(UpdateVoteInfoPayloadS2C::write, UpdateVoteInfoPayloadS2C::new);

    public UpdateVoteInfoPayloadS2C(PacketByteBuf buf) {
        this(buf.readIdentifier(), buf.readOptional(VoteInstance.PACKET_CODEC));
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.identifier);
        buf.writeOptional(this.voteInstance, VoteInstance.PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
