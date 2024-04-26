package com.github.winexp.battlegrounds.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record RupertsTearTeleportPayloadC2S(ItemStack itemStack, Vec3d teleportPos) implements CustomPayload {
    public static final Id<RupertsTearTeleportPayloadC2S> ID = CustomPayload.id("battlegrounds:play/ruperts_tear_teleport");
    public static final PacketCodec<RegistryByteBuf, RupertsTearTeleportPayloadC2S> PACKET_CODEC = CustomPayload.codecOf(RupertsTearTeleportPayloadC2S::write, RupertsTearTeleportPayloadC2S::new);

    public RupertsTearTeleportPayloadC2S(RegistryByteBuf buf) {
        this(ItemStack.PACKET_CODEC.decode(buf), buf.readVec3d());
    }

    public void write(RegistryByteBuf buf) {
        ItemStack.PACKET_CODEC.encode(buf, this.itemStack);
        buf.writeVec3d(this.teleportPos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
