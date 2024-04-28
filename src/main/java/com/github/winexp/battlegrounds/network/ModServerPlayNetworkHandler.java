package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.VoteInfo;
import com.github.winexp.battlegrounds.discussion.vote.VoteInstance;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.network.payload.c2s.play.*;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.*;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.*;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public final class ModServerPlayNetworkHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(SyncVoteInfosPayloadC2S.ID, ModServerPlayNetworkHandler::onSyncVoteInfos);
        ServerPlayNetworking.registerGlobalReceiver(GetVoteInfoPayloadC2S.ID, ModServerPlayNetworkHandler::onGetVoteInfo);
        ServerPlayNetworking.registerGlobalReceiver(VotePayloadC2S.ID, ModServerPlayNetworkHandler::onVote);
        ServerPlayNetworking.registerGlobalReceiver(RupertsTearTeleportPayloadC2S.ID, ModServerPlayNetworkHandler::onRupertsTearTeleport);
    }

    private static void onSyncVoteInfos(SyncVoteInfosPayloadC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        player.server.execute(() -> VoteManager.INSTANCE.syncVoteInfos(player));
    }

    private static void onGetVoteInfo(GetVoteInfoPayloadC2S packet, ServerPlayNetworking.Context context) {
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
        ItemStack stack = packet.itemStack();
        Vec3d pos = player.getPos();
        Vec3d teleportPos = packet.teleportPos();
        ServerWorld world = player.getServerWorld();
        double distance = Math.floor(teleportPos.distanceTo(player.getEyePos()));
        if (player.getInventory().contains(stack) && distance <= RupertsTearItem.MAX_DISTANCE) {
            player.server.execute(() -> {
                int cooldown = (int) (RupertsTearItem.MAX_COOLDOWN * (Math.pow(distance, 1.5) / Math.pow(RupertsTearItem.MAX_DISTANCE, 1.5)));
                player.getItemCooldownManager().set(Items.RUPERTS_TEAR, Math.max(RupertsTearItem.MIN_COOLDOWN, cooldown));
                player.requestTeleport(teleportPos.x, teleportPos.y, teleportPos.z);
                player.onLanding();
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS);

                Vec3d posOffset = teleportPos.subtract(pos);
                double ratio = Math.max(Math.max(Math.abs(posOffset.x), Math.abs(posOffset.z)), Math.abs(posOffset.y));
                Vec3d particleSpeed = posOffset.multiply(1 / ratio).multiply(0.5);
                for (int i = 1; i <= posOffset.length() / particleSpeed.length(); i++) {
                    Vec3d particlePos = pos.add(particleSpeed.multiply(i));
                    ParticleUtil.spawnForceLongParticle(ParticleTypes.WHITE_SMOKE, world, particlePos.x, particlePos.y, particlePos.z, 2, 0.15, 0.15, 0.15, 0.015);
                }
            });
        } else {
            player.getItemCooldownManager().set(Items.RUPERTS_TEAR, RupertsTearItem.FAILED_COOLDOWN);
            player.sendMessage(Text.translatable("item.battlegrounds.ruperts_tear.use_failed"), true);
        }
    }
}
