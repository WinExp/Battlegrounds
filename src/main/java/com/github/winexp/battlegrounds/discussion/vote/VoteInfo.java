package com.github.winexp.battlegrounds.discussion.vote;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VoteInfo {
    public final Identifier identifier;
    public final Text name;
    public final Text description;
    public long timeLeft;

    public VoteInfo(Identifier identifier, Text name, Text description, long timeLeft) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.timeLeft = timeLeft;
    }

    public static VoteInfo readVoteInfoFromBuf(PacketByteBuf buf) {
        return new VoteInfo(buf.readIdentifier(), buf.readText(), buf.readText(), buf.readLong());
    }

    public static void writeVoteInfoToBuf(PacketByteBuf buf, VoteInfo voteInfo) {
        buf.writeIdentifier(voteInfo.identifier);
        buf.writeText(voteInfo.name);
        buf.writeText(voteInfo.description);
        buf.writeLong(voteInfo.timeLeft);
    }
}
