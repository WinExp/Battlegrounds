package com.github.winexp.battlegrounds.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record FlashS2CPacket(Vec3d pos, float distance) implements CustomPayload {
    public static final Id<FlashS2CPacket> ID = CustomPayload.id("battlegrounds:play/flash");
    public static final PacketCodec<PacketByteBuf, FlashS2CPacket> PACKET_CODEC = CustomPayload.codecOf(FlashS2CPacket::write, FlashS2CPacket::new);

    public FlashS2CPacket(PacketByteBuf buf) {
        this(buf.readVec3d(), buf.readFloat());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVec3d(this.pos);
        buf.writeFloat(this.distance);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
