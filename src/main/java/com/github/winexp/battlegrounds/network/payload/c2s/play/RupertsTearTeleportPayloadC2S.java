package com.github.winexp.battlegrounds.network.payload.c2s.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public record RupertsTearTeleportPayloadC2S(Hand hand, Vec3d teleportPos) implements CustomPayload {
    public static final Id<RupertsTearTeleportPayloadC2S> ID = CustomPayload.id("battlegrounds:play/ruperts_tear_teleport");
    public static final PacketCodec<RegistryByteBuf, RupertsTearTeleportPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(RupertsTearTeleportPayloadC2S::write, RupertsTearTeleportPayloadC2S::new);

    public RupertsTearTeleportPayloadC2S(RegistryByteBuf buf) {
        this(Hand.values()[buf.readShort()], buf.readVec3d());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeShort(this.hand.ordinal());
        buf.writeVec3d(this.teleportPos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
