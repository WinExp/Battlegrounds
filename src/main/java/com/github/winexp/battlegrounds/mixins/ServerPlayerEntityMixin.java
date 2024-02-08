package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.events.player.PlayerDamagedCallback;
import com.github.winexp.battlegrounds.events.player.PlayerDeathCallback;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.Variable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity instance = (ServerPlayerEntity) (Object) this;

        ActionResult result = PlayerDamagedCallback.EVENT.invoker().interact(source, instance);
        if (result != ActionResult.PASS) cir.setReturnValue(true);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity instance = (ServerPlayerEntity) (Object) this;

        PlayerDamagedCallback.EVENT.invoker().interact(source, instance);
        PlayerDeathCallback.EVENT.invoker().interact(source, instance);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // 生机勃勃 附魔 状态效果
        ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        int level = EnchantmentHelper.getLevel(Enchantments.VITALITY, stack);
        if (level > 0) {
            Enchantments.VITALITY.giveEffects((ServerPlayerEntity) (Object) this, level);
        }

        // PVP 大佬
        if (player.getEquippedStack(EquipmentSlot.MAINHAND).getItem() == Items.PVP_PRO_SWORD
                || player.getEquippedStack(EquipmentSlot.OFFHAND).getItem() == Items.PVP_PRO_SWORD) {
            Items.PVP_PRO_SWORD.addEffects(player);
        }

        // 自带效果
        if (Variable.INSTANCE.progress.hasEffects) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 2, 4));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 2, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 2, 1));
        }
    }
}
