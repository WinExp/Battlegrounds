package com.github.winexp.battlegrounds.mixins.client;

import com.github.winexp.battlegrounds.client.util.ClientVariables;
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
public class flash_InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (ClientVariables.INSTANCE.flashStrength > 0) {
            renderFlash(context, ClientVariables.INSTANCE.flashStrength);
        }
    }

    @Unique
    private void renderFlash(DrawContext context, float strength) {
        if (ClientVariables.INSTANCE.flashMode != ClientVariables.FlashMode.FILL) return;
        if (strength > 1.0F) strength = 1.0F;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        int alpha = (int) (strength * 255);
        int color = ColorHelper.Argb.getArgb(alpha, 240, 240, 240);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, width, height, color);
    }
}
