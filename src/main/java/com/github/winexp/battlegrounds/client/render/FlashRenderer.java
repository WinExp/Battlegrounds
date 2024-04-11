package com.github.winexp.battlegrounds.client.render;

import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class FlashRenderer implements HudRenderCallback {
    public static final float STRENGTH_LEFT_SPEED = 0.02F;
    public static final float MAX_FLASH_TICKS = 5 * 20;
    private static final Predicate<BlockRaycastResult> BLOCK_PREDICATE = (raycastResult) -> {
        World world = raycastResult.world();
        BlockHitResult hitResult = raycastResult.hitResult();
        BlockPos blockPos = hitResult.getBlockPos();
        return hitResult.getType() == HitResult.Type.MISS
                || WorldUtil.isOpaqueFullCube(world, blockPos);
    };
    private static final BiPredicate<EntityHitResult, BlockRaycastResult> FLASH_PREDICATE = (entityHitResult, raycastResult) -> {
        World world = raycastResult.world();
        BlockPos blockPos = raycastResult.hitResult().getBlockPos();
        return entityHitResult == null && (raycastResult.hitResult().getType() == HitResult.Type.MISS
                || !WorldUtil.isOpaqueFullCube(world, blockPos));
    };
    private float flashStrength;

    public FlashRenderer() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    public static float getFlashStrength(Entity entity, Vec3d flashPos, float distance, float tickDelta) {
        double maxDistance = MathUtil.distanceTo(entity.getPos(), flashPos);
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = MathUtil.getRotationToPos(entity.getEyePos(), flashPos);
        Vec3d playerRotation = entity.getRotationVec(1.0F);
        float rotationOffset = MathUtil.getOffset(vec3d2, playerRotation);
        float rotationAttenuate = Math.max(0, Math.min(distance - 0.1F, rotationOffset - 0.85F));
        EntityHitResult entityHitResult = MathUtil.raycastEntity(entity, flashPos, maxDistance, tickDelta);
        BlockRaycastResult raycastResult1 = MathUtil.raycastBlock(entity, flashPos, vec3d, RaycastContext.FluidHandling.NONE, BLOCK_PREDICATE);
        BlockRaycastResult raycastResult2 = MathUtil.raycastBlock(entity, vec3d, flashPos, RaycastContext.FluidHandling.NONE, BLOCK_PREDICATE);
        if (FLASH_PREDICATE.test(entityHitResult, raycastResult1)
                && FLASH_PREDICATE.test(entityHitResult, raycastResult2)) {
            return (distance - rotationAttenuate) * (MAX_FLASH_TICKS + 20)
                    * STRENGTH_LEFT_SPEED * Math.min(raycastResult1.strength(), raycastResult2.strength());
        } else return 0;
    }

    private void tick(MinecraftClient client) {
        if (this.flashStrength > 0) {
            this.flashStrength -= STRENGTH_LEFT_SPEED;
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
            if (strength > 1.0F) strength = 1.0F;
            int width = context.getScaledWindowWidth();
            int height = context.getScaledWindowHeight();
            int alpha = (int) (strength * 255);
            int color = ColorHelper.Argb.getArgb(alpha, 255, 255, 255);
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, width, height, color);
        }
    }
}
