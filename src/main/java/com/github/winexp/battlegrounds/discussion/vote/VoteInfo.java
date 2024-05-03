package com.github.winexp.battlegrounds.discussion.vote;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VoteInfo {
    public static final PacketByteBuf.PacketReader<VoteInfo> PACKET_READER = (buf) ->
            new VoteInfo(buf.readIdentifier(), buf.readUuid(), buf.readText(), buf.readText(), buf.readBoolean() ? buf.readGameProfile() : null, buf.readInt(), buf.readBoolean());
    public static final PacketByteBuf.PacketWriter<VoteInfo> PACKET_WRITER = (buf, voteInfo) -> {
        buf.writeIdentifier(voteInfo.identifier);
        buf.writeUuid(voteInfo.uuid);
        buf.writeText(voteInfo.name);
        buf.writeText(voteInfo.description);
        if (voteInfo.initiatorProfile != null) {
            buf.writeBoolean(true);
            buf.writeGameProfile(voteInfo.initiatorProfile);
        } else {

            buf.writeBoolean(false);
        }
        buf.writeInt(voteInfo.timeLeft);
        buf.writeBoolean(voteInfo.available);
    };

    public final Identifier identifier;
    public final UUID uuid;
    public final Text name;
    public final Text description;
    @Nullable
    public final GameProfile initiatorProfile;
    public int timeLeft;
    public boolean available;

    public VoteInfo(Identifier identifier, UUID uuid, Text name, Text description, @Nullable GameProfile initiatorProfile, int timeLeft, boolean available) {
        this.identifier = identifier;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.initiatorProfile = initiatorProfile;
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
