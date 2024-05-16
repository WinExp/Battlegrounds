package com.github.winexp.battlegrounds.network.payload.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record PlayerVotedPayloadS2C(Text playerName, VoteInstance voteInstance, boolean result) implements CustomPayload {
    public static final Id<PlayerVotedPayloadS2C> ID = CustomPayload.id("battlegrounds:play/vote/player_voted");
    public static final PacketCodec<PacketByteBuf, PlayerVotedPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(PlayerVotedPayloadS2C::write, PlayerVotedPayloadS2C::new);

    public PlayerVotedPayloadS2C(PacketByteBuf buf) {
        this(TextCodecs.PACKET_CODEC.decode(buf), VoteInstance.PACKET_CODEC.decode(buf), buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        TextCodecs.PACKET_CODEC.encode(buf, this.playerName);
        VoteInstance.PACKET_CODEC.encode(buf, this.voteInstance);
        buf.writeBoolean(this.result);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
