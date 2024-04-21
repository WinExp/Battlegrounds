package com.github.winexp.battlegrounds.util.data;

import net.minecraft.network.PacketByteBuf;

public record ModGameConfig(boolean displayPlayerNameLabel) {
    public static final PacketByteBuf.PacketReader<ModGameConfig> PACKET_READER = (buf) ->
            new ModGameConfig(buf.readBoolean());
    public static final PacketByteBuf.PacketWriter<ModGameConfig> PACKET_WRITER = (buf, config) -> {
        buf.writeBoolean(config.displayPlayerNameLabel);
    };
    public static final ModGameConfig DEFAULT_CONFIG = new ModGameConfig(true);
}
