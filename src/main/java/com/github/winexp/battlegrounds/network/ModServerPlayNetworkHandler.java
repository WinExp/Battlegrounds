package com.github.winexp.battlegrounds.network;

import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.tool.RupertsTearItem;
import com.github.winexp.battlegrounds.network.payload.c2s.play.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class ModServerPlayNetworkHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(RupertsTearTeleportPayloadC2S.ID, ModServerPlayNetworkHandler::onRupertsTearTeleport);
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
