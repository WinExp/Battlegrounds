package com.github.winexp.battlegrounds.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ModVersion(Version version, int protocolVersion) {
    public static final Codec<ModVersion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("version").xmap(string -> {
                try {
                    return Version.parse(string);
                } catch (VersionParsingException e) {
                    throw new RuntimeException(e);
                }
            }, Version::toString).forGetter(ModVersion::version),
            Codec.INT.fieldOf("protocol_version").forGetter(ModVersion::protocolVersion)
    ).apply(instance, ModVersion::new));
    public static final PacketCodec<ByteBuf, ModVersion> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public ModVersion decode(ByteBuf buf) {
            try {
                return new ModVersion(Version.parse(PacketCodecs.STRING.decode(buf)), buf.readInt());
            } catch (VersionParsingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void encode(ByteBuf buf, ModVersion value) {
            PacketCodecs.STRING.encode(buf, value.version.getFriendlyString());
            buf.writeInt(value.protocolVersion);
        }
    };

    @Override
    public String toString() {
        return this.version.getFriendlyString() + '-' + this.protocolVersion;
    }
}
