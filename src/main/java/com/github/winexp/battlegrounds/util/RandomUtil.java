package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.helper.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class RandomUtil {
    public static boolean isSecure(World world, BlockPos pos){
        for (int i = pos.getY() + 1; i <= 320; i++){
            BlockPos pos1 = pos.withY(i);
            BlockState state = world.getBlockState(pos1);
            if (!state.getBlock().canMobSpawnInside(state)) return false;
        }
        return true;
    }

    public static BlockPos getSecureLocation(World world){
        WorldHelper helper = new WorldHelper(world);
        int centerX = helper.getBorderCenterX();
        int centerZ = helper.getBorderCenterZ();
        int size = helper.getBorderSize() - 20;
        if (size > 50000000) size = 50000000;
        Random random = world.random;
        int x = random.nextInt(size) - (size / 2) + centerX;
        int y = 500;
        int z = random.nextInt(size) - (size / 2) + centerZ;
        for (int k = 0; k < 200; k++){
            for (int i = 320; i >= 63; i--){
                BlockPos pos = new BlockPos(x, i, z);
                BlockState state = world.getBlockState(pos);
                if (state.streamTags().anyMatch(BlockTags.VALID_SPAWN::equals)){
                    y = i;
                    break;
                }
            }
            if (y != 500 && isSecure(world, new BlockPos(x, y, z))) break;
            x = random.nextInt(size) - (size / 2) + centerX;
            y = 500;
            z = random.nextInt(size) - (size / 2) + centerZ;
        }
        return new BlockPos(x, y + 1, z);
    }
}
