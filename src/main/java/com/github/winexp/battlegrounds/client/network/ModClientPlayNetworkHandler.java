package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.event.ClientVoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.config.ModGameConfigS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModGameConfigS2CPacket.TYPE, ModClientPlayNetworkHandler::onModGameConfigReceived);
        ClientPlayNetworking.registerGlobalReceiver(SyncVoteInfosS2CPacket.TYPE, ModClientPlayNetworkHandler::onSyncVoteInfos);
        ClientPlayNetworking.registerGlobalReceiver(UpdateVoteInfoS2CPacket.TYPE, ModClientPlayNetworkHandler::onUpdateVoteInfo);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedS2CPacket.TYPE, ModClientPlayNetworkHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedS2CPacket.TYPE, ModClientPlayNetworkHandler::onVoteClosed);
        ClientPlayNetworking.registerGlobalReceiver(PlayerVotedS2CPacket.TYPE, ModClientPlayNetworkHandler::onPlayerVoted);
        ClientPlayNetworking.registerGlobalReceiver(FlashS2CPacket.TYPE, ModClientPlayNetworkHandler::onFlash);
        ClientPlayConnectionEvents.DISCONNECT.register(ModClientPlayNetworkHandler::onPlayDisconnect);
    }

    private static void onModGameConfigReceived(ModGameConfigS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientVariables.gameConfig = packet.config();
    }

    private static void onSyncVoteInfos(SyncVoteInfosS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();
        VoteScreen.syncVoteInfos(client, packet);
    }

    private static void onUpdateVoteInfo(UpdateVoteInfoS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();
        VoteScreen.updateVoteInfo(client, packet);
    }

    private static void onVoteOpened(VoteOpenedS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientVoteEvents.OPENED.invoker().onOpened(packet.voteInfo());
    }

    private static void onVoteClosed(VoteClosedS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientVoteEvents.CLOSED.invoker().onClosed(packet.voteInfo(), packet.closeReason());
    }

    private static void onPlayerVoted(PlayerVotedS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(packet.playerName(), packet.voteInfo(), packet.result());
    }

    private static void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientVariables.resetGameConfig();
    }

    private static void onFlash(FlashS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d pos = packet.pos();
        float distance = packet.distance();
        Entity entity = client.getCameraEntity();
        if (entity != null) {
            float tickDelta = client.getTickDelta();
            float strength = FlashRenderer.calculateFlashStrength(entity, pos, distance, tickDelta);
            if (client.player != null && client.player.isSpectator()) {
                strength = 0.6F;
            }
            ClientConstants.FLASH_RENDERER.setFlashStrength(strength);
        }
    }
}
