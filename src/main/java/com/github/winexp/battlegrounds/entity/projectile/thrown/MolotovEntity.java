package com.github.winexp.battlegrounds.entity.projectile.thrown;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.item.Items;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MolotovEntity extends ThrownItemEntity {
    private static final TrackedData<Integer> FUSE = DataTracker.registerData(MolotovEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public MolotovEntity(net.minecraft.entity.EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public MolotovEntity(LivingEntity livingEntity, World world) {
        super(EntityTypes.MOLOTOV, livingEntity, world);
    }

    public int getFuse() {
        return this.getDataTracker().get(FUSE);
    }

    public void setFuse(int fuse) {
        this.getDataTracker().set(FUSE, fuse);
    }

    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (itemStack.isEmpty() ? ParticleTypes.DRIPPING_LAVA : new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FUSE, 0);
    }

    @Override
    public void tick() {
        if (this.getFuse() >= 0) {
            this.setFuse(this.getFuse() - 1);
            if (this.getFuse() <= 0) {
                BlockHitResult hitResult = BlockHitResult.createMissed(this.getPos(), Direction.UP, this.getBlockPos());
                this.onCollision(hitResult);
            }
        }
        super.tick();
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
        else super.handleStatus(status);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("fuse")) this.setFuse(nbt.getInt("fuse"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("fuse", this.getFuse() < 0 ? -1 : this.getFuse());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 2.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        World world = this.getWorld();
        if (!world.isClient) {
            world.sendEntityStatus(this, (byte) 3);
            AreaEffectCloudEntity areaEffectCloud = new AreaEffectCloudEntity(world, this.getX(), this.getY(), this.getZ());
            areaEffectCloud.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 0));
            areaEffectCloud.setDuration(12 * 20);
            areaEffectCloud.setWaitTime(20);
            areaEffectCloud.setRadius(5.0F);
            areaEffectCloud.setRadiusGrowth((areaEffectCloud.getRadius() * 0.4F) / areaEffectCloud.getDuration());
            areaEffectCloud.setOwner(this.getOwner() == null ? null : (LivingEntity) this.getOwner());
            world.spawnEntity(areaEffectCloud);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.MOLOTOV;
    }
}
