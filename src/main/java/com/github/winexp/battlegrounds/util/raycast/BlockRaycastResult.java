package com.github.winexp.battlegrounds.util.raycast;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record BlockRaycastResult(World world, Entity entity, BlockHitResult hitResult, Vec3d start, Vec3d end, float strength) {
}
