package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.entity.effect.StatusEffectRestraint;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class effectRestraint_StatusEffectMixin {
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;isInfinite()Z"), cancellable = true)
    private void onUpdate(LivingEntity entity, Runnable overwriteCallback, CallbackInfoReturnable<Boolean> cir) {
        StatusEffectInstance targetInstance = (StatusEffectInstance) (Object) this;
        for (StatusEffectInstance statusEffectInstance : entity.getStatusEffects()) {
            if (statusEffectInstance != targetInstance
            && statusEffectInstance.getEffectType() instanceof StatusEffectRestraint effectRestraint
            && !effectRestraint.isStatusEffectAllowed(targetInstance)) {
                cir.setReturnValue(false);
            }
        }
    }
}
