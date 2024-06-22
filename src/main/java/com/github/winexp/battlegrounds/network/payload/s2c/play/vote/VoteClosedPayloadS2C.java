package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.Vote;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record VoteClosedPayloadS2C(UUID uuid, Vote.CloseReason closeReason) implements CustomPayload {
    public static final Id<VoteClosedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/closed");
    public static final PacketCodec<PacketByteBuf, VoteClosedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(VoteClosedPayloadS2C::write, VoteClosedPayloadS2C::new);

    public VoteClosedPayloadS2C(PacketByteBuf buf) {
        this(buf.readUuid(), Vote.CloseReason.PACKET_CODEC.decode(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
        Vote.CloseReason.PACKET_CODEC.encode(buf, this.closeReason);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
