package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record PlayerVotedS2CPacket(Text playerName, VoteInfo voteInfo, boolean result) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/player_voted");
    public static final PacketType<PlayerVotedS2CPacket> TYPE = PacketType.create(ID, PlayerVotedS2CPacket::new);

    public PlayerVotedS2CPacket(PacketByteBuf buf) {
        this(buf.readText(), VoteInfo.PACKET_READER.apply(buf), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeText(this.playerName);
        VoteInfo.PACKET_WRITER.accept(buf, this.voteInfo);
        buf.writeBoolean(this.result);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
