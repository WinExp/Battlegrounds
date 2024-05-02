package com.github.winexp.battlegrounds.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class BlockUtil {
    public static int getOpacity(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getOpacity(world, pos);
    }

    public static float getOpacityFloat(World world, BlockPos pos) {
        return (float) getOpacity(world, pos) / 15;
    }

    public static VoxelShape getCollisionShape(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.getCollisionShape(world, pos);
    }

    public static double getBlockMaxY(World world, BlockPos pos) {
        VoxelShape shape = getCollisionShape(world, pos);
        return shape.getMax(Direction.Axis.Y) + pos.getY();
    }

    public static boolean isOpaque(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOpaque();
    }

    public static boolean isSolidBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isSolidBlock(world, pos);
    }

    public static boolean isSideSolidFullSquare(World world, BlockPos pos, Direction direction) {
        BlockState state = world.getBlockState(pos);
        return state.isSideSolidFullSquare(world, pos, direction);
    }

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
