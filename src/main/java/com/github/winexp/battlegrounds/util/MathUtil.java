package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.util.result.BlockRaycastResult;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MathUtil {
    private final static double RAYCAST_ACCURATE = 10;

    private final static Function<Block, Float> TRANSPARENT_STRENGTH_FUNCTION = (block) -> {
        if (block instanceof LeavesBlock) {
            return 0.95F;
        } else if (block instanceof TintedGlassBlock) {
            return 0.9F;
        } else if (block instanceof TransparentBlock) {
            return 0.98F;
        } else return 0.99F;
    };

    public static Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public static Vec3d getRotationWithEntity(Entity self, Vec3d target) {
        Vec3d vec3d = self.getEyePos();
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = Math.sqrt(d * d + f * f);
        float pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        float yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F);
        return getRotationVector(pitch, yaw);
    }

    public static float distanceTo(Vec3d self, Vec3d target) {
        float f = (float)(self.x - target.x);
        float g = (float)(self.y - target.y);
        float h = (float)(self.z - target.z);
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public static float getOffset(Vec3d start, Vec3d end) {
        Vec3d offsetVec3d = start.subtract(end);
        float x = MathHelper.abs((float) offsetVec3d.x) * 360;
        float y = MathHelper.abs((float) offsetVec3d.y) * 360;
        float z = MathHelper.abs((float) offsetVec3d.z) * 360;
        return MathHelper.sqrt(x * x + y * y + z * z) / 360;
    }

    public static EntityHitResult raycastEntity(Entity entity, double maxDistance, float tickDelta, Vec3d target) {
        double e = maxDistance * maxDistance;
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = getRotationWithEntity(entity, target);
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1.0, 1.0, 1.0);
        return ProjectileUtil.raycast(entity, vec3d, target, box, (entity1) -> !entity1.isSpectator() && entity1.canHit(), e);
    }

    public static BlockRaycastResult raycastBlock(Entity entity, Vec3d start, Vec3d end, RaycastContext.FluidHandling fluidHandling, BiFunction<BlockHitResult, World, Boolean> blockPredicate) {
        World world = entity.getWorld();
        BlockHitResult blockHitResult;
        float strength = 1.0F;
        do {
            blockHitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, entity));
            Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if (blockPredicate == null) return new BlockRaycastResult(blockHitResult, start, end, strength);
            else {
                if (blockPredicate.apply(blockHitResult, world)) {
                    return new BlockRaycastResult(blockHitResult, start, end, strength);
                } else {
                    double distance = end.distanceTo(start);
                    double x = (end.x - start.x) / distance / RAYCAST_ACCURATE;
                    double y = (end.y - start.y) / distance / RAYCAST_ACCURATE;
                    double z = (end.z - start.z) / distance / RAYCAST_ACCURATE;
                    start = blockHitResult.getPos().add(x, y, z);
                    strength *= TRANSPARENT_STRENGTH_FUNCTION.apply(block);
                }
            }
        } while (end.distanceTo(start) > 1 / RAYCAST_ACCURATE);
        return new BlockRaycastResult(blockHitResult, start, end, strength);
    }
}
