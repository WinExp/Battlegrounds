package com.github.winexp.battlegrounds.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("SameReturnValue")
@Mixin(LivingEntity.class)

public class Shield_LivingEntityMixin {
    @Unique
    private float tmp_amount;

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void get_amount(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.tmp_amount = amount;
    }

    @ModifyVariable(method = "damage", at = @At(value = "STORE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V",
            ordinal = 0), name = "amount")
    private float modify_amountOnShieldBlock(float value) {
        return tmp_amount / 2;
    }

    @ModifyVariable(method = "damage", at = @At(value = "STORE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V",
            ordinal = 1), name = "bl")
    private boolean modify_bl_OnShieldBlock(boolean value) {
        return false;
    }
}
