package com.github.winexp.battlegrounds.discussion.vote;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public class VoteInfo {
    public static final PacketCodec<RegistryByteBuf, VoteInfo> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public VoteInfo decode(RegistryByteBuf buf) {
            return new VoteInfo(
                    Identifier.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    TextCodecs.PACKET_CODEC.decode(buf),
                    VoteMode.PACKET_CODEC.decode(buf)
            );
        }

        @Override
        public void encode(RegistryByteBuf buf, VoteInfo value) {
            buf.writeIdentifier(value.group);
            TextCodecs.PACKET_CODEC.encode(buf, value.name);
            TextCodecs.PACKET_CODEC.encode(buf, value.description);
            VoteMode.PACKET_CODEC.encode(buf, value.voteMode);
        }
    };

    private final Identifier group;
    private final Text name;
    private final Text description;
    private final VoteMode voteMode;

    public VoteInfo(Identifier group, Text name, Text description, VoteMode voteMode) {
        this.group = group;
        this.name = name;
        this.description = description;
        this.voteMode = voteMode;
    }

    public Identifier getGroup() {
        return this.group;
    }

    public Text getName() {
        return this.name;
    }

    public Text getDescription() {
        return this.description;
    }

    public VoteMode getVoteMode() {
        return this.voteMode;
    }
}
