package com.github.winexp.battlegrounds.mixins.client;

import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public abstract class flash_BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci, @Local BackgroundRenderer.FogData fogData) {
        if (ClientVariables.INSTANCE.flashMode != ClientVariables.FlashMode.FOG) return;
        float flashStrength = ClientVariables.INSTANCE.flashStrength;
        float flashLeftSpeed = FlashBangEntity.FLASH_STRENGTH_LEFT_SPEED;
        if (flashStrength > 0) {
            fogData.fogShape = FogShape.SPHERE;
            float f = MathHelper.lerp(Math.min(1.0F, (flashStrength / flashLeftSpeed / 20) - (flashLeftSpeed * 20)), viewDistance, FlashBangEntity.FLASH_VISIBILITY);
            if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
                fogData.fogStart = Math.min(fogData.fogStart, 0.0F);
                fogData.fogEnd = Math.min(fogData.fogEnd, f * 0.8F);
            } else {
                fogData.fogStart = Math.min(fogData.fogStart, f * 0.25F);
                fogData.fogEnd = Math.min(fogData.fogEnd, f);
            }
            RenderSystem.setShaderFogStart(fogData.fogStart);
            RenderSystem.setShaderFogEnd(fogData.fogEnd);
            RenderSystem.setShaderFogShape(fogData.fogShape);
            ci.cancel();
        }
    }
}
