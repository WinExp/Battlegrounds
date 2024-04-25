package com.github.winexp.battlegrounds.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record ModGameConfig(boolean displayPlayerNameLabel) {
    public static final Codec<ModGameConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("display_player_name_label").forGetter(ModGameConfig::displayPlayerNameLabel)
    ).apply(instance, ModGameConfig::new));
    public static final PacketCodec<ByteBuf, ModGameConfig> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public ModGameConfig decode(ByteBuf buf) {
            return new ModGameConfig(buf.readBoolean());
        }

        @Override
        public void encode(ByteBuf buf, ModGameConfig value) {
            buf.writeBoolean(value.displayPlayerNameLabel);
        }
    };
}
