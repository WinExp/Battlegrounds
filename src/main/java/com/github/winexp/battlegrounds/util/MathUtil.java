package com.github.winexp.battlegrounds.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
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

    public static EntityHitResult raycastEntity(Entity entity, double maxDistance, float tickDelta, Vec3d target) {
        double e = maxDistance * maxDistance;
        Vec3d vec3d = entity.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = getRotationWithEntity(entity, target);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(maxDistance)).expand(1.0, 1.0, 1.0);
        return ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entity1) -> !entity1.isSpectator() && entity1.canHit(), e);
    }
}
