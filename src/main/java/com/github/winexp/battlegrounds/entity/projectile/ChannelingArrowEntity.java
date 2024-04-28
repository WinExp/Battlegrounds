package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.*;

public class ChannelingArrowEntity extends PersistentProjectileEntity {
    private static final List<StatusEffectInstance> STATUS_EFFECTS = ImmutableList.of(
            new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1),
            new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 0),
            new StatusEffectInstance(StatusEffects.POISON, 5 * 20, 1),
            new StatusEffectInstance(StatusEffects.APPROACHING_EXTINCTION, 5 * 20, 0)
    );
    private static final TrackedData<Boolean> CHANNELING = DataTracker.registerData(ChannelingArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public ChannelingArrowEntity(net.minecraft.entity.EntityType<? extends ChannelingArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public ChannelingArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityTypes.CHANNELING_ARROW, owner, world, stack);
    }

    public static ChannelingArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        return new ChannelingArrowEntity(world, shooter, stack.copyWithCount(1));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(CHANNELING, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.1, 1);
                }
            } else {
                ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.15, 1);
            }
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);

        for (StatusEffectInstance effectInstance : STATUS_EFFECTS) {
            target.addStatusEffect(new StatusEffectInstance(effectInstance), this.getOwner());
        }
        if (this.getChanneling()) {
            LightningEntity lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(target.getWorld());
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(target.getPos());
                if (this.getOwner() instanceof ServerPlayerEntity player) {
                    lightning.setChanneler(player);
                }
                target.getWorld().spawnEntity(lightning);
                this.getWorld().sendEntityStatus(this, (byte) 3);
                this.playSound(SoundEvents.ITEM_TRIDENT_THUNDER, 5, 1);
            }
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("channeling", NbtElement.BYTE_TYPE)) this.setChanneling(nbt.getBoolean("channeling"));
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("channeling", this.getChanneling());
    }

    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 20);
            ParticleUtil.addParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.3, 30);
        } else {
            super.handleStatus(status);
        }
    }

    public boolean getChanneling() {
        return this.getDataTracker().get(CHANNELING);
    }

    public void setChanneling(boolean channeling) {
        this.getDataTracker().set(CHANNELING, channeling);
    }
}
