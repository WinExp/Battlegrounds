package com.github.winexp.battlegrounds.util;

import net.fabricmc.loader.api.Version;
import net.minecraft.network.PacketByteBuf;

public record ModVersion(Version version, int protocolVersion) {
    public static PacketByteBuf.PacketReader<ModVersion> PACKET_READER = (buf) -> {
        try {
            return new ModVersion(Version.parse(buf.readString()), buf.readInt());
        } catch (Exception e) {
            return null;
        }
    };
    public static PacketByteBuf.PacketWriter<ModVersion> PACKET_WRITER = (buf, modVersion) -> {
        buf.writeString(modVersion.version.toString());
        buf.writeInt(modVersion.protocolVersion);
    };
}
