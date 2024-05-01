package com.github.winexp.battlegrounds.network.payload.c2s.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModInfoPayloadC2S(ModVersion modVersion) implements CustomPayload {
    public static final Id<ModInfoPayloadC2S> ID = CustomPayload.id("battlegrounds:config/mod_info");
    public static final PacketCodec<PacketByteBuf, ModInfoPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(ModInfoPayloadC2S::write, ModInfoPayloadC2S::new);

    public ModInfoPayloadC2S(PacketByteBuf buf) {
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