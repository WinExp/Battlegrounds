package com.github.winexp.battlegrounds.util;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class RandomUtil {
    public static boolean isSecure(World world, BlockPos pos) {
        if (!world.getWorldBorder().contains(pos)) return false;
        for (int i = pos.getY() + 1; i <= 320; i++) {
            BlockPos pos1 = pos.withY(i);
            if (!WorldUtil.canMobSpawnInside(world, pos1)) return false;
        }
        return true;
    }

    public static BlockPos getSecureLocation(World world) {
        WorldBorder border = world.getWorldBorder();
        Vec3i start = new Vec3i((int) border.getBoundWest(), world.getSeaLevel(), (int) border.getBoundNorth());
        Vec3i end = new Vec3i((int) border.getBoundEast(), world.getTopY(), (int) border.getBoundSouth());
        BlockBox box = BlockBox.create(start, end);
        return getSecureLocation(world, box);
    }

    public static BlockPos getSecureLocation(World world, BlockBox box) {
        Random random = world.getRandom();
        int x = random.nextBetween(box.getMinX(), box.getMaxX());
        int y = box.getMaxY() + 1;
        int z = random.nextBetween(box.getMinZ(), box.getMaxZ());
        for (int k = 0; k < 200; k++) {
            for (int i = box.getMaxY(); i >= box.getMinY(); i--) {
                BlockPos pos = new BlockPos(x, i, z);
                BlockState state = world.getBlockState(pos);
                if (state.streamTags().anyMatch(BlockTags.VALID_SPAWN::equals)) {
                    y = i;
                    break;
                }
            }
            if (y != 500 && isSecure(world, new BlockPos(x, y, z))) break;
            x = random.nextBetween(box.getMinX(), box.getMaxX());
            y = 500;
            z = random.nextBetween(box.getMinZ(), box.getMaxZ());
        }
        return new BlockPos(x, y + 1, z);
    }
}
