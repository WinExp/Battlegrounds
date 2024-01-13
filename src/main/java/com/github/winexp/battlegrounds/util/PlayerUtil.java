package com.github.winexp.battlegrounds.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerUtil {
    public static void randomTeleport(World world, ServerPlayerEntity player){
        BlockPos pos = RandomUtil.getSecureLocation(world);
        player.teleport((ServerWorld) world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
    }
}
