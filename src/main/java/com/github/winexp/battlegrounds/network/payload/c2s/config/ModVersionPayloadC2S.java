package com.github.winexp.battlegrounds.network.payload.c2s.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModVersionPayloadC2S(ModVersion modVersion) implements CustomPayload {
    public static final Id<ModVersionPayloadC2S> ID = CustomPayload.id("battlegrounds:config/mod_version");
    public static final PacketCodec<PacketByteBuf, ModVersionPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(ModVersionPayloadC2S::write, ModVersionPayloadC2S::new);

    public ModVersionPayloadC2S(PacketByteBuf buf) {
        this(ModVersion.PACKET_CODEC.decode(buf));
    }

    public void write(PacketByteBuf buf) {
        ModVersion.PACKET_CODEC.encode(buf, this.modVersion);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
