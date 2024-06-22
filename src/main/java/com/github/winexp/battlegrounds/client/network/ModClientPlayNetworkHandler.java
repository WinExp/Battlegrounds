package com.github.winexp.battlegrounds.client.network;

import com.github.winexp.battlegrounds.client.config.ClientRootConfig;
import com.github.winexp.battlegrounds.client.render.FlashRenderer;
import com.github.winexp.battlegrounds.client.toast.vote.VoteClosedToast;
import com.github.winexp.battlegrounds.client.toast.vote.VoteOpenedToast;
import com.github.winexp.battlegrounds.client.util.ClientConstants;
import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.client.vote.ClientVoteManager;
import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.network.payload.s2c.play.FlashPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.config.ModGameConfigPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.*;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public final class ModClientPlayNetworkHandler {
    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register(ModClientPlayNetworkHandler::onDisconnect);
        ClientPlayNetworking.registerGlobalReceiver(ModGameConfigPayloadS2C.ID, ModClientPlayNetworkHandler::onModGameConfigReceived);
        ClientPlayNetworking.registerGlobalReceiver(FlashPayloadS2C.ID, ModClientPlayNetworkHandler::onFlash);
        ClientPlayNetworking.registerGlobalReceiver(PlayerVotedPayloadS2C.ID, ModClientPlayNetworkHandler::onPlayerVoted);
        ClientPlayNetworking.registerGlobalReceiver(SyncVotesPayloadS2C.ID, ModClientPlayNetworkHandler::onSyncVotes);
        ClientPlayNetworking.registerGlobalReceiver(VoteOpenedPayloadS2C.ID, ModClientPlayNetworkHandler::onVoteOpened);
        ClientPlayNetworking.registerGlobalReceiver(VoteClosedPayloadS2C.ID, ModClientPlayNetworkHandler::onVoteClosed);
        ClientPlayNetworking.registerGlobalReceiver(VoteUpdatedPayloadS2C.ID, ModClientPlayNetworkHandler::onVoteUpdated);
    }

    private static void onDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientVariables.resetGameConfig();
    }

    private static void onModGameConfigReceived(ModGameConfigPayloadS2C payload, ClientPlayNetworking.Context context) {
        ClientVariables.gameConfig = payload.config();
    }

    private static void onFlash(FlashPayloadS2C payload, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();
        Vec3d pos = payload.pos();
        float distance = payload.distance();
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

    private static void onPlayerVoted(PlayerVotedPayloadS2C payload, ClientPlayNetworking.Context context) {
    }

    private static void onSyncVotes(SyncVotesPayloadS2C payload, ClientPlayNetworking.Context context) {
        ClientVoteManager.INSTANCE.onSyncVotes(payload.votes());
    }

    private static void onVoteOpened(VoteOpenedPayloadS2C payload, ClientPlayNetworking.Context context) {
        Vote vote = ClientVoteManager.INSTANCE.getVote(payload.uuid());
        if (vote == null) return;
        if (ClientRootConfig.HANDLER.instance().showVoteOpenedNotification) {
            context.client().getToastManager().add(new VoteOpenedToast(vote));
        }
        PlayerEntity player = context.client().player;
        if (player != null && ClientRootConfig.HANDLER.instance().playVoteOpenedSounds) {
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0F, 1.0F);
        }
    }

    private static void onVoteClosed(VoteClosedPayloadS2C payload, ClientPlayNetworking.Context context) {
        Vote vote = ClientVoteManager.INSTANCE.getVote(payload.uuid());
        if (vote == null) return;
        if (ClientRootConfig.HANDLER.instance().showVoteClosedNotification) {
            context.client().getToastManager().add(new VoteClosedToast(vote, payload.closeReason()));
        }
        PlayerEntity player = context.client().player;
        if (player != null && ClientRootConfig.HANDLER.instance().playVoteClosedSounds) {
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0F, 2.0F);
        }
    }

    private static void onVoteUpdated(VoteUpdatedPayloadS2C payload, ClientPlayNetworking.Context context) {
        ClientVoteManager.INSTANCE.onVoteUpdated(payload.uuid(), payload.vote().orElse(null));
    }
}
