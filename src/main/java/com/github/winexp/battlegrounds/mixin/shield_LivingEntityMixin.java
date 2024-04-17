package com.github.winexp.battlegrounds.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class shield_LivingEntityMixin {
    @Unique
    private boolean shieldDamaged = false;

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Shadow
    protected abstract void takeShieldHit(LivingEntity attacker);

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    private boolean isBlockedByShield(LivingEntity instance, DamageSource source) {
        if (this.shieldDamaged) return false;
        else return instance.blockedByShield(source);
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeShieldHit(Lnet/minecraft/entity/LivingEntity;)V"))
    private void takeShieldHit(LivingEntity instance, LivingEntity attacker) {
        if (!this.shieldDamaged) this.takeShieldHit(attacker);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"))
    private void onDamageShield(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!this.shieldDamaged) {
            this.shieldDamaged = true;
            this.damage(source, amount * 0.5F);
            this.shieldDamaged = true;
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamageReturn(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.shieldDamaged = false;
    }
}
