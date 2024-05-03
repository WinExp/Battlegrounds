package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.network.packet.c2s.play.*;
import com.github.winexp.battlegrounds.network.packet.c2s.play.vote.*;
import com.github.winexp.battlegrounds.network.packet.s2c.play.vote.*;
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
        ServerPlayNetworking.registerGlobalReceiver(SyncVoteInfosC2SPacket.TYPE, ModServerPlayNetworkHandler::onSyncVoteInfos);
        ServerPlayNetworking.registerGlobalReceiver(GetVoteInfoC2SPacket.TYPE, ModServerPlayNetworkHandler::onGetVoteInfo);
        ServerPlayNetworking.registerGlobalReceiver(VoteC2SPacket.TYPE, ModServerPlayNetworkHandler::onVote);
        ServerPlayNetworking.registerGlobalReceiver(RupertsTearTeleportC2SPacket.TYPE, ModServerPlayNetworkHandler::onRupertsTearTeleport);
    }

    private static void onSyncVoteInfos(SyncVoteInfosC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        player.server.execute(() -> VoteManager.INSTANCE.syncVoteInfos(player));
    }

    private static void onGetVoteInfo(GetVoteInfoC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        VoteManager.INSTANCE.getVoteInstance(packet.voteId()).ifPresentOrElse(voteInstance -> {
            VoteInfo voteInfo = voteInstance.getVoteInfo(player);
            UpdateVoteInfoS2CPacket responsePacket = new UpdateVoteInfoS2CPacket(Optional.of(voteInfo));
            sender.sendPacket(responsePacket);
        }, () -> {
            UpdateVoteInfoS2CPacket responsePacket = new UpdateVoteInfoS2CPacket(Optional.empty());
            sender.sendPacket(responsePacket);
        });
    }

    private static void onVote(VoteC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
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
                UpdateVoteInfoS2CPacket responsePacket = new UpdateVoteInfoS2CPacket(Optional.of(voteInfo));
                responseSender.sendPacket(responsePacket);
            }
        }
    }

    private static void onRupertsTearTeleport(RupertsTearTeleportC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {
        Vec3d teleportPos = packet.teleportPos();
        Hand hand = packet.hand();
        ItemStack stack = player.getStackInHand(hand);
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
