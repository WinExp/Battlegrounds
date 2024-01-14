package com.github.winexp.battlegrounds.util;

import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerUtil {
    public static void sendTitle(ServerPlayerEntity player, Text title){
        sendTitle(player, title, Text.of(""));
    }

    public static void sendTitle(ServerPlayerEntity player, Text title, Text subtitle){
        player.networkHandler.sendPacket(new TitleS2CPacket(title));
        player.networkHandler.sendPacket(new SubtitleS2CPacket(subtitle));
        player.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 10, 15));
    }

    public static void randomTeleport(World world, ServerPlayerEntity player){
        BlockPos pos = RandomUtil.getSecureLocation(world);
        player.teleport((ServerWorld) world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
    }
}
