package com.github.winexp.battlegrounds.network.payload.s2c.play.config;

import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModGameConfigPayloadS2C(ModGameConfig config) implements CustomPayload {
    public static final Id<ModGameConfigPayloadS2C> ID = CustomPayload.id("battlegrounds:config/mod_game_config");
    public static final PacketCodec<PacketByteBuf, ModGameConfigPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(ModGameConfigPayloadS2C::write, ModGameConfigPayloadS2C::new);

    public ModGameConfigPayloadS2C(PacketByteBuf buf) {
        this(ModGameConfig.PACKET_CODEC.decode(buf));
    }

    public void write(PacketByteBuf buf) {
        ModGameConfig.PACKET_CODEC.encode(buf, this.config);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
