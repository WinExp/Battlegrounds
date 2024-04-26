package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.gui.screen.vote.VoteScreen;
import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.event.ClientVoteEvents;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashPayloadS2C;
import com.github.winexp.battlegrounds.network.packet.s2c.play.config.ModGameConfigPayloadS2C;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModGameConfigPayloadS2C.ID, ModClientPlayNetworkHandler::onModGameConfigReceived);
        ClientPlayNetworking.registerGlobalReceiver(SyncVoteInfosPayloadS2C.ID, ModClientPlayNetworkHandler::onSyncVoteInfos);
        ClientPlayNetworking.registerGlobalReceiver(UpdateVoteInfoPayloadS2C.ID, ModClientPlayNetworkHandler::onUpdateVoteInfo);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedPayloadS2C.ID, ModClientPlayNetworkHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedPayloadS2C.ID, ModClientPlayNetworkHandler::onVoteClosed);
        ClientPlayNetworking.registerGlobalReceiver(PlayerVotedPayloadS2C.ID, ModClientPlayNetworkHandler::onPlayerVoted);
        ClientPlayNetworking.registerGlobalReceiver(FlashPayloadS2C.ID, ModClientPlayNetworkHandler::onFlash);
        ClientPlayConnectionEvents.DISCONNECT.register(ModClientPlayNetworkHandler::onPlayDisconnect);
    }

    private static void onModGameConfigReceived(ModGameConfigPayloadS2C packet, ClientPlayNetworking.Context context) {
        ClientVariables.gameConfig = packet.config();
    }

    private static void onSyncVoteInfos(SyncVoteInfosPayloadS2C packet, ClientPlayNetworking.Context context) {
        VoteScreen.onSyncVoteInfos(context.client(), packet);
    }

    private static void onUpdateVoteInfo(UpdateVoteInfoPayloadS2C packet, ClientPlayNetworking.Context context) {
        VoteScreen.onUpdateVoteInfo(context.client(), packet);
    }

    private static void onVoteOpened(VoteOpenedPayloadS2C packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.OPENED.invoker().onOpened(packet.voteInfo());
    }

    private static void onVoteClosed(VoteClosedPayloadS2C packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.CLOSED.invoker().onClosed(packet.voteInfo(), packet.closeReason());
    }

    private static void onPlayerVoted(PlayerVotedPayloadS2C packet, ClientPlayNetworking.Context context) {
        ClientVoteEvents.PLAYER_VOTED.invoker().onPlayerVoted(packet.playerName(), packet.voteInfo(), packet.result());
    }

    private static void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientVariables.resetGameConfig();
    }

    private static void onFlash(FlashPayloadS2C packet, ClientPlayNetworking.Context context) {
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
