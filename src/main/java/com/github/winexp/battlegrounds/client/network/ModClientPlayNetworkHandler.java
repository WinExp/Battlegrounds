package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.event.ClientVoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void registerReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(FlashS2CPacket.TYPE, ModClientPlayNetworkHandler::onFlash);
        ClientPlayNetworking.registerGlobalReceiver(SyncVoteInfosS2CPacket.TYPE, ModClientPlayNetworkHandler::onSyncVoteInfos);
        ClientPlayNetworking.registerGlobalReceiver(UpdateVoteInfoS2CPacket.TYPE, ModClientPlayNetworkHandler::onUpdateVoteInfo);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedS2CPacket.TYPE, ModClientPlayNetworkHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedS2CPacket.TYPE, ModClientPlayNetworkHandler::onVoteClosed);
        ClientPlayNetworking.registerGlobalReceiver(PlayerVotedS2CPacket.TYPE, ModClientPlayNetworkHandler::onPlayerVoted);
    }

    private static void onFlash(FlashS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d pos = packet.pos();
        float distance = packet.distance();
        Entity entity = client.getCameraEntity();
        if (entity != null) {
            float tickDelta = client.getTickDelta();
            ClientVariables.flashStrength = Math.max(ClientVariables.flashStrength,
                    FlashBangEntity.getFlashStrength(entity, pos, distance, tickDelta));
        }
    }

    private static void onSyncVoteInfos(SyncVoteInfosS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        VoteScreen.onSyncVoteInfos(client, packet);
    }

    private static void onUpdateVoteInfo(UpdateVoteInfoS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient client = MinecraftClient.getInstance();
        VoteScreen.onUpdateVoteInfo(client, packet);
    }

    private static void onVoteOpened(VoteOpenedS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ClientVoteEvents.OPENED.invoker().onOpened(packet.voteInfo());
    }

    private static void onVoteClosed(VoteClosedS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ClientVoteEvents.CLOSED.invoker().onClosed(packet.voteInfo(), packet.closeReason());
    }

    private static void onPlayerVoted(PlayerVotedS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ClientVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(packet.playerName(), packet.voteInfo(), packet.result());
    }
}
