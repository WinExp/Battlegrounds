package com.github.winexp.battlegrounds.network.payload.s2c.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModInfoPayloadS2C(ModVersion modVersion) implements CustomPayload {
    public static final Id<ModInfoPayloadS2C> ID = CustomPayload.id("battlegrounds:config/mod_info");
    public static final PacketCodec<PacketByteBuf, ModInfoPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(ModInfoPayloadS2C::write, ModInfoPayloadS2C::new);

    public ModInfoPayloadS2C(PacketByteBuf buf) {
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
