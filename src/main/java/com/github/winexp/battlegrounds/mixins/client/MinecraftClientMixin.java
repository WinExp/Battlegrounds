package com.github.winexp.battlegrounds.mixins.client;

import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (ClientVariables.INSTANCE.flashStrength > 0) {
            ClientVariables.INSTANCE.flashStrength -= FlashBangEntity.STRENGTH_LEFT_SPEED;
        }
        else if (ClientVariables.INSTANCE.flashStrength < 0){
            ClientVariables.INSTANCE.flashStrength = 0;
        }
    }
}
