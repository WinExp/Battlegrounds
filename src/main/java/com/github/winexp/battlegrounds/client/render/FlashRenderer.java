package com.github.winexp.battlegrounds.client.render;

import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.github.winexp.battlegrounds.event.ClientApplyFogCallback;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FlashRenderer implements HudRenderCallback, ClientApplyFogCallback {
    private float flashStrength;

    public FlashRenderer() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(MinecraftClient client) {
        if (this.flashStrength > 0) {
            this.flashStrength -= FlashBangEntity.STRENGTH_LEFT_SPEED;
        } else if (this.flashStrength < 0){
            this.flashStrength = 0;
        }
    }

    public float getFlashStrength() {
        return this.flashStrength;
    }

    public void setFlashStrength(float flashStrength) {
        this.flashStrength = Math.max(flashStrength, this.flashStrength);
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        float strength = this.flashStrength;
        if (strength > 0) {
            RenderSystem.enableBlend();
            if (strength > 1.0F) strength = 1.0F;
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();
            int alpha = (int) (strength * 255);
            int color = ColorHelper.Argb.getArgb(alpha, 240, 240, 240);
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, width, height, color);
        }
    }

    @Override
    public void onApplyFog(float viewDistance, BackgroundRenderer.FogData fogData) {
        float flashStrength = this.flashStrength;
        float flashLeftSpeed = FlashBangEntity.STRENGTH_LEFT_SPEED;
        if (flashStrength > 0) {
            fogData.fogShape = FogShape.SPHERE;
            float f = MathHelper.lerp(Math.min(1.0F, (flashStrength / flashLeftSpeed / 20) - (flashLeftSpeed * 20)), viewDistance, FlashBangEntity.FOG_VISIBILITY);
            if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
                fogData.fogStart = Math.min(fogData.fogStart, 0.0F);
                fogData.fogEnd = Math.min(fogData.fogEnd, f * 0.8F);
            } else {
                fogData.fogStart = Math.min(fogData.fogStart, f * 0.25F);
                fogData.fogEnd = Math.min(fogData.fogEnd, f);
            }
        }
    }
}
