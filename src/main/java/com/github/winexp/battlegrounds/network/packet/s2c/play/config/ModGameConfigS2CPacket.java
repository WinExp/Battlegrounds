package com.github.winexp.battlegrounds.network.packet.s2c.play.config;

import com.github.winexp.battlegrounds.util.data.ModGameConfig;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ModGameConfigS2CPacket(ModGameConfig config) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "config/mod_game_config");
    public static final PacketType<ModGameConfigS2CPacket> TYPE = PacketType.create(ID, ModGameConfigS2CPacket::new);

    public ModGameConfigS2CPacket(PacketByteBuf buf) {
        this(ModGameConfig.PACKET_READER.apply(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        ModGameConfig.PACKET_WRITER.accept(buf, this.config);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
