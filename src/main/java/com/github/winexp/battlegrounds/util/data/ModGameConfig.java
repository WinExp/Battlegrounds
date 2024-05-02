package com.github.winexp.battlegrounds.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;

public record ModGameConfig(boolean displayPlayerNameLabel) {
    public static final Codec<ModGameConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("display_player_name_label").forGetter(ModGameConfig::displayPlayerNameLabel)
    ).apply(instance, ModGameConfig::new));
    public static final PacketByteBuf.PacketReader<ModGameConfig> PACKET_READER = (buf) ->
            new ModGameConfig(buf.readBoolean());
    public static final PacketByteBuf.PacketWriter<ModGameConfig> PACKET_WRITER = (buf, config) ->
            buf.writeBoolean(config.displayPlayerNameLabel);
}
