package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.event.ClientVoteEvents;
import com.github.winexp.battlegrounds.network.packet.c2s.play.RupertsTearTeleportC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.GetVoteInfoC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.SyncVoteInfosC2SPacket;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.VoteC2SPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.config.ModGameConfigS2CPacket;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(GetVoteInfoC2SPacket.ID, GetVoteInfoC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SyncVoteInfosC2SPacket.ID, SyncVoteInfosC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(VoteC2SPacket.ID, VoteC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RupertsTearTeleportC2SPacket.ID, RupertsTearTeleportC2SPacket.PACKET_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ModGameConfigS2CPacket.ID, ModClientPlayNetworkHandler::onModGameConfigReceived);
        ClientPlayNetworking.registerGlobalReceiver(SyncVoteInfosS2CPacket.ID, ModClientPlayNetworkHandler::onSyncVoteInfos);
        ClientPlayNetworking.registerGlobalReceiver(UpdateVoteInfoS2CPacket.ID, ModClientPlayNetworkHandler::onUpdateVoteInfo);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedS2CPacket.ID, ModClientPlayNetworkHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedS2CPacket.ID, ModClientPlayNetworkHandler::onVoteClosed);
        ClientPlayNetworking.registerGlobalReceiver(PlayerVotedS2CPacket.ID, ModClientPlayNetworkHandler::onPlayerVoted);
        ClientPlayNetworking.registerGlobalReceiver(FlashS2CPacket.ID, ModClientPlayNetworkHandler::onFlash);
        ClientPlayConnectionEvents.DISCONNECT.register(ModClientPlayNetworkHandler::onPlayDisconnect);
    }

    private static void onModGameConfigReceived(ModGameConfigS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientVariables.gameConfig = packet.config();
    }

    private static void onSyncVoteInfos(SyncVoteInfosS2CPacket packet, ClientPlayNetworking.Context context) {
        VoteScreen.onSyncVoteInfos(context.client(), packet);
    }

    private static void onUpdateVoteInfo(UpdateVoteInfoS2CPacket packet, ClientPlayNetworking.Context context) {
        VoteScreen.onUpdateVoteInfo(context.client(), packet);
    }

    private static void onVoteOpened(VoteOpenedS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.OPENED.invoker().onOpened(packet.voteInfo());
    }

    private static void onVoteClosed(VoteClosedS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.CLOSED.invoker().onClosed(packet.voteInfo(), packet.closeReason());
    }

    private static void onPlayerVoted(PlayerVotedS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(packet.playerName(), packet.voteInfo(), packet.result());
    }

    private static void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientVariables.resetGameConfig();
    }

    private static void onFlash(FlashS2CPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
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
