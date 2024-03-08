package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.events.ModServerPlayerEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class naturalRegen_PlayerEntityMixin {
    @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    private void canHeal(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        boolean result = ModServerPlayerEvents.ALLOW_NATURAL_REGEN.invoker().allow(player);
        cir.setReturnValue(result);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void peacefulHeal(PlayerEntity instance, float v) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        boolean result = ModServerPlayerEvents.ALLOW_NATURAL_REGEN.invoker().allow(player);
        if (result) {
            player.heal(v);
        }
    }
}
