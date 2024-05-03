package com.github.winexp.battlegrounds.network.payload.s2c.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModVersionPayloadS2C(ModVersion modVersion) implements CustomPayload {
    public static final Id<ModVersionPayloadS2C> ID = CustomPayload.id("battlegrounds:config/mod_version");
    public static final PacketCodec<PacketByteBuf, ModVersionPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(ModVersionPayloadS2C::write, ModVersionPayloadS2C::new);

    public ModVersionPayloadS2C(PacketByteBuf buf) {
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
