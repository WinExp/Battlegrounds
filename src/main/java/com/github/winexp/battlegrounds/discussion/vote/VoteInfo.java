package com.github.winexp.battlegrounds.discussion.vote;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class VoteInfo {
    public static final PacketCodec<ByteBuf, VoteInfo> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public VoteInfo decode(ByteBuf buf) {
            return new VoteInfo(
                    Identifier.PACKET_CODEC.decode(buf),
                    PacketByteBuf.readUuid(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    buf.readInt(),
                    buf.readBoolean()
            );
        }

        @Override
        public void encode(ByteBuf buf, VoteInfo value) {
            Identifier.PACKET_CODEC.encode(buf, value.identifier);
            PacketByteBuf.writeUuid(buf, value.uuid);
            TextCodecs.PACKET_CODEC.encode(buf, value.name);
            TextCodecs.PACKET_CODEC.encode(buf, value.description);
            TextCodecs.PACKET_CODEC.encode(buf, value.initiatorName);
            buf.writeInt(value.timeLeft);
            buf.writeBoolean(value.available);
        }
    };

    public final Identifier identifier;
    public final UUID uuid;
    public final Text name;
    public final Text description;
    public final Text initiatorName;
    public int timeLeft;
    public boolean available;

    public VoteInfo(Identifier identifier, UUID uuid, Text name, Text description, Text initiatorName, int timeLeft, boolean available) {
        this.identifier = identifier;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.initiatorName = initiatorName;
        this.timeLeft = timeLeft;
        this.available = available;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VoteInfo voteInfo)) return false;
        else if (!this.identifier.equals(voteInfo.identifier)) return false;
        else return this.uuid.equals(voteInfo.uuid);
    }

    @Override
    public int hashCode() {
        return 14 * this.identifier.hashCode() + 3 * this.uuid.hashCode();
    }
}
