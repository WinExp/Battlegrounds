package com.github.winexp.battlegrounds.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.UUID;

public interface ModPacketCodecs {
    PacketCodec<ByteBuf, UUID> UUID = PacketCodec.tuple(PacketCodecs.VAR_LONG, java.util.UUID::getMostSignificantBits, PacketCodecs.VAR_LONG, java.util.UUID::getLeastSignificantBits, java.util.UUID::new);
}
