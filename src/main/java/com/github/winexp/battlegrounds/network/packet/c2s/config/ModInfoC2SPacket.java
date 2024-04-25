package com.github.winexp.battlegrounds.network.packet.c2s.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModInfoC2SPacket(ModVersion modVersion) implements CustomPayload {
    public static final Id<ModInfoC2SPacket> ID = CustomPayload.id("battlegrounds:config/mod_info");
    public static final PacketCodec<PacketByteBuf, ModInfoC2SPacket> PACKET_CODEC = CustomPayload.codecOf(ModInfoC2SPacket::write, ModInfoC2SPacket::new);

    public ModInfoC2SPacket(PacketByteBuf buf) {
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
