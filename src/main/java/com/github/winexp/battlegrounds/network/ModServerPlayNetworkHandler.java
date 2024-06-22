package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.discussion.vote.Vote;
import com.github.winexp.battlegrounds.discussion.vote.VoteManager;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.network.payload.c2s.play.*;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.QueryVotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.SyncVotesPayloadC2S;
import com.github.winexp.battlegrounds.network.payload.c2s.play.vote.VotePayloadC2S;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.SyncVotesPayloadS2C;
import com.github.winexp.battlegrounds.network.payload.s2c.play.vote.VoteUpdatedPayloadS2C;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public final class ModServerPlayNetworkHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RupertsTearTeleportPayloadC2S.ID, ModServerPlayNetworkHandler::onRupertsTearTeleport);
        ServerPlayNetworking.registerGlobalReceiver(VotePayloadC2S.ID, ModServerPlayNetworkHandler::onVote);
        ServerPlayNetworking.registerGlobalReceiver(QueryVotePayloadC2S.ID, ModServerPlayNetworkHandler::onQueryVote);
        ServerPlayNetworking.registerGlobalReceiver(SyncVotesPayloadC2S.ID, ModServerPlayNetworkHandler::onSyncVotes);
    }

    private static void onRupertsTearTeleport(RupertsTearTeleportPayloadC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        Hand hand = payload.hand();
        ItemStack stack = player.getStackInHand(hand);
        Vec3d teleportPos = payload.teleportPos();
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

    private static void onVote(VotePayloadC2S payload, ServerPlayNetworking.Context context) {
        Vote vote = VoteManager.INSTANCE.getVote(payload.uuid());
        if (vote != null) {
            vote.vote(context.player(), payload.result());
        }
    }

    private static void onQueryVote(QueryVotePayloadC2S payload, ServerPlayNetworking.Context context) {
        Optional<Vote> vote = Optional.ofNullable(VoteManager.INSTANCE.getVote(payload.uuid()));
        context.responseSender().sendPacket(new VoteUpdatedPayloadS2C(payload.uuid(), vote));
    }

    private static void onSyncVotes(SyncVotesPayloadC2S payload, ServerPlayNetworking.Context context) {
        context.responseSender().sendPacket(new SyncVotesPayloadS2C(VoteManager.INSTANCE.getVotes().stream().limit(200).toList()));
    }
}
