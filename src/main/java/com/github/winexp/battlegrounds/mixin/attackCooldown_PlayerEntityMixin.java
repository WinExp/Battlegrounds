package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.entity.player.PlayerEntityAttackCooldownGetter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class attackCooldown_PlayerEntityMixin implements PlayerEntityAttackCooldownGetter {
    @Shadow
    public abstract float getAttackCooldownProgress(float baseTime);

    @Unique
    private float lastAttackCooldown;

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void getLastAttackCooldown(Entity target, CallbackInfo ci) {
        this.lastAttackCooldown = this.getAttackCooldownProgress(0.5F);
    }

    @Unique
    @Override
    public float battlegrounds$getLastAttackCooldown() {
        return this.lastAttackCooldown;
    }
}
