package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.network.packet.s2c.FlashS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.VoteInfosS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public final class ModClientNetworkPlayHandler {
    public static void onFlash(FlashS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d pos = packet.pos();
        float distance = packet.distance();
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                float tickDelta = client.getTickDelta();
                ClientVariables.flashStrength = Math.max(ClientVariables.flashStrength,
                        FlashBangEntity.getFlashStrength(entity, tickDelta, pos, distance));
            }
        });
    }

    public static void onReturnVoteInfos(VoteInfosS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        VoteScreen.voteInfoCallback(MinecraftClient.getInstance(), packet);
    }
}
