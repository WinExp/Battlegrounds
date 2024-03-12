package com.github.winexp.battlegrounds.network.packet.c2s;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SyncVoteInfosC2SPacket() implements FabricPacket {
    public static final Identifier ID = new Identifier("battlegrounds", "sync_vote_infos");
    public static final PacketType<SyncVoteInfosC2SPacket> TYPE = PacketType.create(ID, SyncVoteInfosC2SPacket::new);

    public SyncVoteInfosC2SPacket(PacketByteBuf buf) {
        this();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
