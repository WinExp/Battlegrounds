package com.github.winexp.battlegrounds.util.result;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public record BlockRaycastResult(BlockHitResult hitResult, Vec3d start, Vec3d end, float strength) {
}
