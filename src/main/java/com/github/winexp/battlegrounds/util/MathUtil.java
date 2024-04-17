package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.util.raycast.BlockRaycastResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class MathUtil {
    public static final BiFunction<World, BlockPos, Float> NONE_STRENGTH_FUNCTION =
            (world, pos) -> 1.0F;
    public static final Predicate<BlockRaycastResult> NONE_ABORT_PREDICATE = (raycastResult) -> true;
    private static final double RAYCAST_ACCURATE = 5;

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

    public static float getRotationOffset(Vec3d from, Vec3d to) {
        Vec3d offsetVec3d = from.subtract(to);
        float x = MathHelper.abs((float) offsetVec3d.x) * 360;
        float y = MathHelper.abs((float) offsetVec3d.y) * 360;
        float z = MathHelper.abs((float) offsetVec3d.z) * 360;
        return MathHelper.sqrt(x * x + y * y + z * z) / 360;
    }

    @NotNull
    public static BlockRaycastResult raycastBlock(Entity entity, Vec3d begin, Vec3d end, RaycastContext.ShapeType shapeType, RaycastContext.FluidHandling fluidHandling, Predicate<BlockRaycastResult> abortPredicate, BiFunction<World, BlockPos, Float> strengthFunction) {
        World world = entity.getWorld();
        BlockHitResult blockHitResult;
        float strength = 1.0F;
        do {
            blockHitResult = world.raycast(new RaycastContext(begin, end, shapeType, fluidHandling, entity));
            if (abortPredicate == null) return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
            else {
                if (abortPredicate.test(new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength))) {
                    return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
                } else {
                    double distance = end.distanceTo(begin);
                    double x = (end.x - begin.x) / distance / RAYCAST_ACCURATE;
                    double y = (end.y - begin.y) / distance / RAYCAST_ACCURATE;
                    double z = (end.z - begin.z) / distance / RAYCAST_ACCURATE;
                    begin = blockHitResult.getPos().add(x, y, z);
                    float transparent = strengthFunction.apply(world, blockHitResult.getBlockPos());
                    transparent = (float) Math.pow(transparent, 1.0F / RAYCAST_ACCURATE);
                    strength *= transparent;
                }
            }
        } while (end.distanceTo(begin) > 1 / RAYCAST_ACCURATE || strength <= 0);
        return new BlockRaycastResult(world, entity, blockHitResult, begin, end, strength);
    }
}
