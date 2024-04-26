package com.github.winexp.battlegrounds.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record FlashPayloadS2C(Vec3d pos, float distance) implements CustomPayload {
    public static final Id<FlashPayloadS2C> ID = CustomPayload.id("battlegrounds:play/flash");
    public static final PacketCodec<PacketByteBuf, FlashPayloadS2C> PACKET_CODEC = CustomPayload.codecOf(FlashPayloadS2C::write, FlashPayloadS2C::new);

    public FlashPayloadS2C(PacketByteBuf buf) {
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
