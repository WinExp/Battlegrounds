package com.github.winexp.battlegrounds.util;

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtil {
    public static void addParticlesWithOffset(World world, ParticleEffect particle, double x, double y, double z, double posOffset, double speedOffset, int amount) {
        if (world.isClient) {
            Random random = world.getRandom();
            for (int i = 0; i < amount; i++) {
                double posX = RandomUtil.nextDoubleBetween(random, -posOffset, posOffset) + x;
                double posY = RandomUtil.nextDoubleBetween(random, -posOffset, posOffset) + y;
                double posZ = RandomUtil.nextDoubleBetween(random, -posOffset, posOffset) + z;
                double speedX = RandomUtil.nextDoubleBetween(random, -speedOffset, speedOffset);
                double speedY = RandomUtil.nextDoubleBetween(random, -speedOffset, speedOffset);
                double speedZ = RandomUtil.nextDoubleBetween(random, -speedOffset, speedOffset);
                world.addParticle(particle, posX, posY, posZ, speedX, speedY, speedZ);
            }
        }
    }

    public static <T extends ParticleEffect> void spawnForceLongParticle(T particle, ServerWorld world, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, true, x, y, z, (float) deltaX, (float) deltaY, (float) deltaZ, (float) speed, count);
        for (ServerPlayerEntity player : world.getPlayers()) {
            world.sendToPlayerIfNearby(player, true, x, y, z, particleS2CPacket);
        }
    }
}
