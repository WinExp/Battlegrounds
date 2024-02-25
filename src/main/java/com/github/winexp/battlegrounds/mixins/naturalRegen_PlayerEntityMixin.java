package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.util.PlayerUtil;
import com.github.winexp.battlegrounds.util.Variables;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class naturalRegen_PlayerEntityMixin {
    @Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
    private void canHeal(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (Variables.progress.players.get(PlayerUtil.getUUID(player)) != null
                && !Variables.progress.players.get(PlayerUtil.getUUID(player)).naturalRegen) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;heal(F)V"))
    private void peacefulHeal(PlayerEntity instance, float v) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (Variables.progress.players.get(PlayerUtil.getUUID(player)) == null
                || Variables.progress.players.get(PlayerUtil.getUUID(player)).naturalRegen) {
            player.heal(v);
        }
    }
}
