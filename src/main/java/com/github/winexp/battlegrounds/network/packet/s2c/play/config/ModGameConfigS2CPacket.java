package com.github.winexp.battlegrounds.network.packet.s2c.play.config;

import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ModGameConfigS2CPacket(ModGameConfig config) implements CustomPayload {
    public static final Id<ModGameConfigS2CPacket> ID = CustomPayload.id("battlegrounds:config/mod_game_config");
    public static final PacketCodec<PacketByteBuf, ModGameConfigS2CPacket> PACKET_CODEC = CustomPayload.codecOf(ModGameConfigS2CPacket::write, ModGameConfigS2CPacket::new);

    public ModGameConfigS2CPacket(PacketByteBuf buf) {
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
