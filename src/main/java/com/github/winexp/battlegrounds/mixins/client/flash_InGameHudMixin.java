package com.github.winexp.battlegrounds.mixins.client;

import com.github.winexp.battlegrounds.client.util.ClientVariables;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class flash_InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ClientVariables.flashStrength > 0) {
            renderFlash(context, ClientVariables.flashStrength);
        }
    }

    @Unique
    private void renderFlash(DrawContext context, float strength) {
        RenderSystem.enableBlend();
        if (strength > 1.0F) strength = 1.0F;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int alpha = (int) (strength * 255);
        int color = ColorHelper.Argb.getArgb(alpha, 240, 240, 240);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, width, height, color);
    }
}
