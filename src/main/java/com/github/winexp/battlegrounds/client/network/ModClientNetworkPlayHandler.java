package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.toast.VoteClosedToast;
import com.github.winexp.battlegrounds.client.toast.VoteOpenedToast;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.network.packet.s2c.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public final class ModClientNetworkPlayHandler {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(FlashS2CPacket.TYPE, ModClientNetworkPlayHandler::onFlash);
        ClientPlayNetworking.registerGlobalReceiver(SyncVoteInfosS2CPacket.TYPE, ModClientNetworkPlayHandler::onSyncVoteInfos);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedPacket.TYPE, ModClientNetworkPlayHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedPacket.TYPE, ModClientNetworkPlayHandler::onVoteClosed);
    }

    private static void onFlash(FlashS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d pos = packet.pos();
        float distance = packet.distance();
        client.execute(() -> {
            Entity entity = client.getCameraEntity();
            if (entity != null) {
                float tickDelta = client.getTickDelta();
                ClientVariables.flashStrength = Math.max(ClientVariables.flashStrength,
                        FlashBangEntity.getFlashStrength(entity, pos, distance, tickDelta));
            }
        });
    }

    private static void onVoteOpened(VoteOpenedPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getToastManager().add(new VoteOpenedToast(packet));
        VoteScreen.onVoteOpened(client, packet);
    }

    private static void onVoteClosed(VoteClosedPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.getToastManager().add(new VoteClosedToast(packet));
        VoteScreen.onVoteClosed(client, packet);
    }

    private static void onSyncVoteInfos(SyncVoteInfosS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        VoteScreen.syncVoteInfoCallback(client, packet);
    }
}
