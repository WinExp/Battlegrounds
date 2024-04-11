package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class MathUtil {
    private static final double RAYCAST_ACCURATE = 10;

    private static final BiFunction<World, BlockPos, Float> TRANSPARENT_STRENGTH_FUNCTION = (world, blockPos) -> {
        Block block = world.getBlockState(blockPos).getBlock();
        if (block instanceof LeavesBlock) {
            return 0.7F;
        } else if (block instanceof TintedGlassBlock) {
            return 0.6F;
        } else if (block instanceof TransparentBlock) {
            return 0.85F;
        } else return Math.max(1.0F - WorldUtil.getOpacityFloat(world, blockPos), 0.1F);
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

    public static Vec3d getRotationToPos(Vec3d start, Vec3d target) {
        double d = target.x - start.x;
        double e = target.y - start.y;
        double f = target.z - start.z;
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

    public static EntityHitResult raycastEntity(Entity entity, Vec3d end, double maxDistance, float tickDelta) {
        double e = maxDistance * maxDistance;
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = getRotationToPos(entity.getEyePos(), end);
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1.0, 1.0, 1.0);
        return ProjectileUtil.raycast(entity, vec3d, end, box, (entity1) -> !entity1.isSpectator() && entity1.canHit(), e);
    }

    @NotNull
    public static BlockRaycastResult raycastBlock(Entity entity, Vec3d begin, Vec3d end, RaycastContext.FluidHandling fluidHandling, Predicate<BlockRaycastResult> blockPredicate) {
        World world = entity.getWorld();
        BlockHitResult blockHitResult;
        float strength = 1.0F;
        do {
            blockHitResult = world.raycast(new RaycastContext(begin, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, entity));
            if (blockPredicate == null) return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
            else {
                if (blockPredicate.test(new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength))) {
                    return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
                } else {
                    double distance = end.distanceTo(begin);
                    double x = (end.x - begin.x) / distance / RAYCAST_ACCURATE;
                    double y = (end.y - begin.y) / distance / RAYCAST_ACCURATE;
                    double z = (end.z - begin.z) / distance / RAYCAST_ACCURATE;
                    begin = blockHitResult.getPos().add(x, y, z);
                    float transparent = TRANSPARENT_STRENGTH_FUNCTION.apply(world, blockHitResult.getBlockPos());
                    transparent = (float) Math.pow(transparent, 1.0F / RAYCAST_ACCURATE);
                    strength *= transparent;
                }
            }
        } while (end.distanceTo(begin) > 1 / RAYCAST_ACCURATE || strength <= 0);
        return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
    }
}
