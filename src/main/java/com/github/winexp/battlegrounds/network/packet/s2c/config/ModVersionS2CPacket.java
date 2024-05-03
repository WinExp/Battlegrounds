package com.github.winexp.battlegrounds.network.packet.s2c.config;

import com.github.winexp.battlegrounds.util.data.ModVersion;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record ModVersionS2CPacket(ModVersion modVersion) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "config/mod_version");
    public static final PacketType<ModVersionS2CPacket> TYPE = PacketType.create(ID, ModVersionS2CPacket::new);

    public ModVersionS2CPacket(PacketByteBuf buf) {
        this(ModVersion.PACKET_READER.apply(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        ModVersion.PACKET_WRITER.accept(buf, this.modVersion);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
