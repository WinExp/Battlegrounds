package com.github.winexp.battlegrounds.network.packet.c2s.play;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record RupertsTearTeleportC2SPacket(ItemStack itemStack, Vec3d teleportPos) implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "play/ruperts_tear_teleport");
    public static final PacketType<RupertsTearTeleportC2SPacket> TYPE = PacketType.create(ID, RupertsTearTeleportC2SPacket::new);

    public RupertsTearTeleportC2SPacket(PacketByteBuf buf) {
        this(buf.readItemStack(), buf.readVec3d());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeItemStack(this.itemStack);
        buf.writeVec3d(this.teleportPos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
