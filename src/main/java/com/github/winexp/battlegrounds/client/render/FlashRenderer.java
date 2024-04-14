package com.github.winexp.battlegrounds.client.render;

import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.WorldUtil;
import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class FlashRenderer implements HudRenderCallback {
    public static final float STRENGTH_LEFT_SPEED = 0.02F;
    public static final float MAX_FLASH_TICKS = 5 * 20;
    private static final BiFunction<World, BlockPos, Float> TRANSPARENCY_STRENGTH_FUNCTION = (world, blockPos) -> {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block instanceof LeavesBlock) {
            return 0.7F;
        } else if (block instanceof TintedGlassBlock) {
            return 0.6F;
        } else if (block instanceof TransparentBlock) {
            return 0.85F;
        } else {
            float transparency = 1.0F - WorldUtil.getOpacityFloat(world, blockPos);
            if (transparency == 0) return 0F;
            else return Math.max(transparency, 0.1F);
        }
    };
    private static final Predicate<BlockRaycastResult> ABORT_PREDICATE = (raycastResult) -> {
        World world = raycastResult.world();
        BlockHitResult hitResult = raycastResult.hitResult();
        BlockPos blockPos = hitResult.getBlockPos();
        return hitResult.getType() == HitResult.Type.MISS
        || TRANSPARENCY_STRENGTH_FUNCTION.apply(world, blockPos) == 0F;
    };
    private static final Predicate<BlockRaycastResult> FLASH_PREDICATE = (raycastResult) -> {
        World world = raycastResult.world();
        BlockHitResult hitResult = raycastResult.hitResult();
        BlockPos blockPos = hitResult.getBlockPos();
        return raycastResult.hitResult().getType() == HitResult.Type.MISS
                || TRANSPARENCY_STRENGTH_FUNCTION.apply(world, blockPos) > 0F;
    };

    private float flashStrength;

    public FlashRenderer() {
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    public static float getFlashStrength(Entity entity, Vec3d flashPos, float distance, float tickDelta) {
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = MathUtil.getRotationToPos(entity.getEyePos(), flashPos);
        Vec3d playerRotation = entity.getRotationVec(1.0F);
        float rotationOffset = MathUtil.getRotationOffset(vec3d2, playerRotation);
        float rotationAttenuate = Math.max(0, Math.min(distance - 0.1F, rotationOffset - 0.7F));
        BlockRaycastResult raycastResult = MathUtil.raycastBlock(entity, flashPos, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ABORT_PREDICATE, TRANSPARENCY_STRENGTH_FUNCTION);
        if (FLASH_PREDICATE.test(raycastResult)) {
            return (distance - rotationAttenuate) * (MAX_FLASH_TICKS + 20)
                    * STRENGTH_LEFT_SPEED * raycastResult.strength();
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
