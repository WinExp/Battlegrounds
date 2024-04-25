package com.github.winexp.battlegrounds.network.packet.c2s.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record RupertsTearTeleportC2SPacket(ItemStack itemStack, Vec3d teleportPos) implements CustomPayload {
    public static final Id<RupertsTearTeleportC2SPacket> ID = CustomPayload.id("battlegrounds:play/ruperts_tear_teleport");
    public static final PacketCodec<RegistryByteBuf, RupertsTearTeleportC2SPacket> PACKET_CODEC = CustomPayload.codecOf(RupertsTearTeleportC2SPacket::write, RupertsTearTeleportC2SPacket::new);

    public RupertsTearTeleportC2SPacket(RegistryByteBuf buf) {
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
