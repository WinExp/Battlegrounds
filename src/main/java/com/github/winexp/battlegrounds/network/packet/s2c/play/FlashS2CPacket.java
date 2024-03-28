package com.github.winexp.battlegrounds.network.packet.s2c.play;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record FlashS2CPacket(Vec3d pos, float distance) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/flash");
    public static final PacketType<FlashS2CPacket> TYPE = PacketType.create(ID, FlashS2CPacket::new);

    public FlashS2CPacket(PacketByteBuf buf) {
        this(buf.readVec3d(), buf.readFloat());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVec3d(this.pos);
        buf.writeFloat(this.distance);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
