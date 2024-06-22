package com.github.winexp.battlegrounds.util;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class RandomUtil {
    public static float nextFloatBetween(Random random, float begin, float end) {
        return random.nextFloat() * (end - begin) + begin;
    }

    public static double nextDoubleBetween(Random random, double begin, double end) {
        return random.nextDouble() * (end - begin) + begin;
    }

    public static boolean isSecure(World world, BlockBox box, BlockPos pos) {
        if (!box.contains(pos.getX(), pos.getY(), pos.getZ())) return false;
        for (int i = pos.getY() + 1; i <= box.getMaxY(); i++) {
            BlockPos pos1 = pos.withY(i);
            if (!BlockUtil.canMobSpawnInside(world, pos1)) return false;
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
        int maxY = box.getMaxY();
        int minY = box.getMinY();
        int chunkX = ChunkSectionPos.getSectionCoord(random.nextBetween(box.getMinX(), box.getMaxX()));
        int chunkZ = ChunkSectionPos.getSectionCoord(random.nextBetween(box.getMinZ(), box.getMaxZ()));
        long begin = System.currentTimeMillis();
        for (int k = 0; k < 200; k++) {
            for (int x = ChunkSectionPos.getBlockCoord(chunkX); x <= ChunkSectionPos.getBlockCoord(chunkX + 1) - 1; x++) {
                for (int z = ChunkSectionPos.getBlockCoord(chunkZ); z <= ChunkSectionPos.getBlockCoord(chunkZ + 1) - 1; z++) {
                    if (!box.contains(x, maxY, z)) continue;
                    for (int i = maxY; i >= minY; i--) {
                        BlockPos pos = new BlockPos(x, i, z);
                        BlockState state = world.getBlockState(pos);
                        if (state.isIn(BlockTags.VALID_SPAWN)
                        && isSecure(world, box, pos)) {
                            long end = System.currentTimeMillis();
                            Constants.LOGGER.info("随机传送耗时 {}ms", end - begin);
                            return new BlockPos(x, i + 1, z);
                        }
                    }
                }
            }
            chunkX = ChunkSectionPos.getSectionCoord(random.nextBetween(box.getMinX(), box.getMaxX()));
            chunkZ = ChunkSectionPos.getSectionCoord(random.nextBetween(box.getMinZ(), box.getMaxZ()));
        }
        BlockPos centerPos = box.getCenter();
        return new BlockPos(centerPos.getX(), maxY, centerPos.getX());
    }
}
