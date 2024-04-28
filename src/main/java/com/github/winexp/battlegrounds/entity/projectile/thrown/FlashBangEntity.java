package com.github.winexp.battlegrounds.entity.projectile.thrown;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.network.payload.s2c.play.FlashPayloadS2C;
import com.github.winexp.battlegrounds.sound.SoundEvents;
import com.github.winexp.battlegrounds.util.MathUtil;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import com.github.winexp.battlegrounds.util.RandomUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;

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
        if (!world.isClient) {
            List<ServerPlayerEntity> players = world.getPlayers().stream().map(player ->
                    (ServerPlayerEntity) player).toList();
            for (ServerPlayerEntity player : players) {
                float distance = MathUtil.distanceTo(pos, player.getEyePos());
                if (distance <= MAX_DISTANCE + DISTANCE_INCREMENT) {
                    float distanceStrength = (MAX_DISTANCE - distance + DISTANCE_INCREMENT) / MAX_DISTANCE;
                    FlashPayloadS2C packet = new FlashPayloadS2C(pos, distanceStrength);
                    ServerPlayNetworking.send(player, packet);
                }
            }
        }
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
        return false;
    }

    @Override
    public float getEntityReboundDamage() {
        return 2.0F;
    }

    @Override
    protected void spawnTriggerParticles() {
        ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 20);
        ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.3, 30);
    }

    @Override
    protected void playTriggerSound() {
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
            world.createExplosion(this, Explosion.createDamageSource(world, this),
                    new EntityExplosionBehavior(this), this.getX(), this.getY(), this.getZ(), 1.0F, false, World.ExplosionSourceType.NONE,
                    ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, RegistryEntry.of(SoundEvents.ENTITY_FLASH_BANG_EXPLODE));
            summonFlash(world, this.getPos());
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FLASH_BANG;
    }
}
