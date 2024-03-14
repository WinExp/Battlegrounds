package com.github.winexp.battlegrounds.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtil {
    public static boolean isFullCube(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isFullCube(world, pos);
    }

    public static boolean isOpaqueFullCube(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOpaqueFullCube(world, pos);
    }

    public static boolean canMobSpawnInside(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock().canMobSpawnInside(state);
    }
}
