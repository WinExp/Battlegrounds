package com.github.winexp.battlegrounds.network.packet.s2c.play.vote;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteSettings;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record VoteClosedS2CPacket(VoteInfo voteInfo, VoteSettings.CloseReason closeReason) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/vote/vote_closed");
    public static final PacketType<VoteClosedS2CPacket> TYPE = PacketType.create(ID, VoteClosedS2CPacket::new);

    public VoteClosedS2CPacket(PacketByteBuf buf) {
        this(VoteInfo.PACKET_READER.apply(buf), buf.readEnumConstant(VoteSettings.CloseReason.class));
    }

    @Override
    public void write(PacketByteBuf buf) {
        VoteInfo.PACKET_WRITER.accept(buf, this.voteInfo);
        buf.writeEnumConstant(closeReason);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
