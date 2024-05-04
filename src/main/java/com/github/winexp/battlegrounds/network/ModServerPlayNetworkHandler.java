package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.network.payload.c2s.play.*;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.*;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.*;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public final class ModServerPlayNetworkHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SyncVoteInfosPayloadC2S.ID, ModServerPlayNetworkHandler::onSyncVoteInfos);
        ServerPlayNetworking.registerGlobalReceiver(UpdateVoteInfoPayloadC2S.ID, ModServerPlayNetworkHandler::onGetVoteInfo);
        ServerPlayNetworking.registerGlobalReceiver(VotePayloadC2S.ID, ModServerPlayNetworkHandler::onVote);
        ServerPlayNetworking.registerGlobalReceiver(RupertsTearTeleportPayloadC2S.ID, ModServerPlayNetworkHandler::onRupertsTearTeleport);
    }

    private static void onSyncVoteInfos(SyncVoteInfosPayloadC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        player.server.execute(() -> VoteManager.INSTANCE.syncVoteInfos(player));
    }

    private static void onGetVoteInfo(UpdateVoteInfoPayloadC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PacketSender sender = context.responseSender();
        VoteManager.INSTANCE.getVoteInstance(packet.voteId()).ifPresentOrElse(voteInstance -> {
            VoteInfo voteInfo = voteInstance.getVoteInfo(player);
            UpdateVoteInfoPayloadS2C responsePacket = new UpdateVoteInfoPayloadS2C(Optional.of(voteInfo));
            sender.sendPacket(responsePacket);
        }, () -> {
            UpdateVoteInfoPayloadS2C responsePacket = new UpdateVoteInfoPayloadS2C(Optional.empty());
            sender.sendPacket(responsePacket);
        });
    }

    private static void onVote(VotePayloadC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PacketSender sender = context.responseSender();
        Identifier identifier = packet.identifier();
        if (VoteManager.INSTANCE.isVoting(identifier)) {
            Optional<VoteInstance> optionalVoteInstance = VoteManager.INSTANCE.getVoteInstance(identifier);
            if (optionalVoteInstance.isEmpty()) return;
            VoteInstance voteInstance = optionalVoteInstance.get();
            if (packet.result()) {
                voteInstance.acceptVote(player);
            } else {
                voteInstance.denyVote(player);
            }
            if (voteInstance.isVoting()) {
                VoteInfo voteInfo = voteInstance.getVoteInfo(player);
                UpdateVoteInfoPayloadS2C responsePacket = new UpdateVoteInfoPayloadS2C(Optional.of(voteInfo));
                sender.sendPacket(responsePacket);
            }
        }
    }

    private static void onRupertsTearTeleport(RupertsTearTeleportPayloadC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        Hand hand = packet.hand();
        ItemStack stack = player.getStackInHand(hand);
        Vec3d teleportPos = packet.teleportPos();
        double distance = Math.floor(teleportPos.distanceTo(player.getEyePos()));
        if (distance <= RupertsTearItem.MAX_DISTANCE) {
            if (!stack.isOf(Items.RUPERTS_TEAR) || stack.getDamage() >= stack.getMaxDamage()) return;
            player.server.execute(() -> RupertsTearItem.teleport(player, teleportPos, distance));
            RupertsTearItem.damageStack(player, hand);
        } else {
            player.getItemCooldownManager().set(Items.RUPERTS_TEAR, RupertsTearItem.FAILED_COOLDOWN);
            player.sendMessage(Text.translatable("item.battlegrounds.ruperts_tear.use_failed"), true);
        }
    }
}
