package com.github.winexp.battlegrounds.mixins;

import com.github.winexp.battlegrounds.events.player.PlayerDamagedCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamaged(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        ServerPlayerEntity instance = (ServerPlayerEntity) (Object) this;

        ActionResult result = PlayerDamagedCallback.EVENT.invoker().interact(source, instance);
        if (result != ActionResult.PASS) cir.setReturnValue(true);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onPlayerDeath(DamageSource source, CallbackInfo ci){
        ServerPlayerEntity instance = (ServerPlayerEntity) (Object) this;

        PlayerDamagedCallback.EVENT.invoker().interact(source, instance);
    }
}
