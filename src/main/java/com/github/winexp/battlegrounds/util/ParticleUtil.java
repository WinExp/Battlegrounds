package com.github.winexp.battlegrounds.util;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtil {
    public static void spawnParticlesWithOffset(World world, ParticleEffect particle, double x, double y, double z, double posOffset, double speedOffset, int amount) {
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
}
