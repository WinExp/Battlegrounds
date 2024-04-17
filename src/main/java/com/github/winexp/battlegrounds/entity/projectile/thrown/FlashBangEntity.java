package com.github.winexp.battlegrounds.entity.projectile.thrown;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.packet.s2c.play.FlashS2CPacket;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class FlashBangEntity extends AbstractThrownPropEntity {
    public static final int MAX_DISTANCE = 48;
    public static final int DISTANCE_INCREMENT = 16;

    public FlashBangEntity(net.minecraft.entity.EntityType<? extends AbstractThrownPropEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlashBangEntity(World world, double d, double e, double f) {
        super(EntityTypes.FLASH_BANG, d, e, f, world);
    }

    public FlashBangEntity(LivingEntity livingEntity, World world) {
        super(EntityTypes.FLASH_BANG, livingEntity, world);
    }

    public static void summonFlash(World world, Vec3d pos) {
        List<? extends PlayerEntity> players = world.getPlayers();
        for (PlayerEntity player1 : players) {
            ServerPlayerEntity player = (ServerPlayerEntity) player1;
            float distance = MathUtil.distanceTo(pos, player.getEyePos());
            if (distance <= MAX_DISTANCE + DISTANCE_INCREMENT) {
                float distanceStrength = (MAX_DISTANCE - distance + DISTANCE_INCREMENT) / MAX_DISTANCE;
                FlashS2CPacket packet = new FlashS2CPacket(pos, distanceStrength);
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    @Override
    public float getEntityReboundDamage() {
        return 2.0F;
    }

    @Override
    protected void spawnTriggerParticles() {
        super.spawnTriggerParticles();
        ParticleUtil.spawnParticlesWithOffset(this.getWorld(), ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 1, 4);
    }

    @Override
    protected void playTriggerSound() {
        this.playSound(SoundEvents.ENTITY_FLASH_BANG_EXPLODE, 2, 1.0F);
    }

    @Override
    protected void playReboundSound() {
        Random random = this.getWorld().getRandom();
        this.playSound(SoundEvents.ENTITY_FLASH_BANG_REBOUND, 1.5F, RandomUtil.nextFloatBetween(random, 0.95F, 1.05F));
    }

    @Override
    protected void onTrigger() {
        World world = this.getWorld();
        if (!world.isClient) {
            summonFlash(world, this.getPos());
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }
}
