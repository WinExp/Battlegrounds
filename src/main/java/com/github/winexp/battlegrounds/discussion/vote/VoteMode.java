package com.github.winexp.battlegrounds.discussion.vote;

import com.github.winexp.battlegrounds.registry.ModRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public abstract class VoteMode {
    public static final PacketCodec<RegistryByteBuf, VoteMode> PACKET_CODEC = PacketCodec.recursive(codec -> PacketCodecs.registryValue(ModRegistryKeys.VOTE_MODE));

    public abstract boolean canAccept(Vote vote);

    public abstract boolean canTerminate(Vote vote);
}
