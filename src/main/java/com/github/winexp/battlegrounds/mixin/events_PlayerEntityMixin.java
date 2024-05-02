package com.github.winexp.battlegrounds.mixin;

import com.github.winexp.battlegrounds.event.ModServerPlayerEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class events_PlayerEntityMixin {
    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V", shift = At.Shift.AFTER))
    private void onPlayerDamaged(DamageSource source, float amount, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModServerPlayerEvents.AFTER_PLAYER_DAMAGED.invoker().onPlayerDamaged(serverPlayer, source, amount);
        }
    }

    @Inject(method = "canFoodHeal", at = @At("RETURN"), cancellable = true)
    private void canHeal(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            boolean result = cir.getReturnValue() && ModServerPlayerEvents.ALLOW_NATURAL_REGEN.invoker().allowPlayerNaturalRegen(serverPlayer);
            cir.setReturnValue(result);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void peacefulHeal(PlayerEntity instance, float v) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) {
            boolean result = ModServerPlayerEvents.ALLOW_NATURAL_REGEN.invoker().allowPlayerNaturalRegen(serverPlayer);
            if (result) {
                player.heal(v);
            }
        }
    }
}
