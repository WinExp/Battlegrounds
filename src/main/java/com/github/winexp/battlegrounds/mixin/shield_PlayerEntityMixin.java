package com.github.winexp.battlegrounds.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class shield_PlayerEntityMixin {
    @Unique
    private DamageSource lastDamageSource;
    @Unique
    private boolean inShieldDamage = false;

    @Shadow
    public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.lastDamageSource = source;
    }

    @Inject(method = "damageShield", at = @At("HEAD"))
    private void onShieldDamage(float amount, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (!this.inShieldDamage) {
            this.inShieldDamage = true;
            if (player.isBlocking()) {
                player.clearActiveItem();
            }
            this.damage(this.lastDamageSource, amount * 0.5F);
            this.inShieldDamage = false;
        }
    }
}
