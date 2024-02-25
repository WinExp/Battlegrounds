package com.github.winexp.battlegrounds.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtil {
    public static boolean isSolidBlock(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isSolidBlock(world, pos);
    }

    public static boolean isTransparent(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isTransparent(world, pos);
    }
}
