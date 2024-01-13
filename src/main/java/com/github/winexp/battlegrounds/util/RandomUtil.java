package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.Battlegrounds;
import com.github.winexp.battlegrounds.helper.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class RandomUtil {
    public static boolean isSecure(World world, BlockPos pos){
        for (int i = pos.getY() + 1; i <= 320; i++){
            BlockPos pos1 = pos.withY(i);
            BlockState state = world.getBlockState(pos1);
            if (state.isSolidBlock(world, pos1)) return false;
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
        int x = 0;
        int y = -100;
        int z = 0;
        for (int k = 0; k < 200; k++){
            x = random.nextInt(size) - (size / 2) + centerX;
            y = -100;
            z = random.nextInt(size) - (size / 2) + centerZ;
            for (int i = 320; i >= 63; i--){
                BlockPos pos = new BlockPos(x, i, z);
                BlockState state = world.getBlockState(pos);
                if (state.isOpaqueFullCube(world, pos)){
                    y = i;
                    break;
                }
            }
            if (y != -100 && isSecure(world, new BlockPos(x, y, z))) break;
        }
        return new BlockPos(x, y + 1, z);
    }
}
