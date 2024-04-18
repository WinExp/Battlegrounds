package com.github.winexp.battlegrounds.entity.projectile;

import com.github.winexp.battlegrounds.entity.EntityTypes;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.util.ParticleUtil;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChannelingArrowEntity extends PersistentProjectileEntity {
    private static final Map<StatusEffect, Integer> STATUS_EFFECTS = Map.of(
            StatusEffects.SLOWNESS, 1,
            StatusEffects.WEAKNESS, 0,
            StatusEffects.POISON, 1,
            StatusEffects.APPROACHING_EXTINCTION, 0
    );
    private static final int STATUS_DURATION = 5 * 20;
    private static final ItemStack DEFAULT_STACK = new ItemStack(Items.ARROW);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(ChannelingArrowEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CHANNELING = DataTracker.registerData(ChannelingArrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final Set<StatusEffectInstance> effects;
    private Potion potion;
    private boolean colorSet;

    public ChannelingArrowEntity(net.minecraft.entity.EntityType<? extends ChannelingArrowEntity> entityType, World world) {
        super(entityType, world, DEFAULT_STACK);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public ChannelingArrowEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityTypes.CHANNELING_ARROW, owner, world, stack);
        this.potion = Potions.EMPTY;
        this.effects = Sets.newHashSet();
    }

    public static ChannelingArrowEntity createArrow(World world, ItemStack stack, LivingEntity shooter) {
        ChannelingArrowEntity arrow = new ChannelingArrowEntity(world, shooter, stack.copyWithCount(1));
        arrow.initFromStack(stack);
        return arrow;
    }

    public static int getCustomPotionColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.contains("CustomPotionColor", 99) ? nbtCompound.getInt("CustomPotionColor") : -1;
    }

    public void initFromStack(ItemStack stack) {
        if (stack.isOf(Items.TIPPED_ARROW)) {
            this.potion = PotionUtil.getPotion(stack);
            Collection<StatusEffectInstance> collection = PotionUtil.getCustomPotionEffects(stack);
            if (!collection.isEmpty()) {
                for (StatusEffectInstance statusEffectInstance : collection) {
                    this.effects.add(new StatusEffectInstance(statusEffectInstance));
                }
            }

            int i = getCustomPotionColor(stack);
            if (i == -1) {
                this.initColor();
            } else {
                this.setColor(i);
            }
        } else if (stack.isOf(Items.ARROW)) {
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }

    }

    private void initColor() {
        this.colorSet = false;
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.dataTracker.set(COLOR, -1);
        } else {
            this.dataTracker.set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }

    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(COLOR, -1);
        this.dataTracker.startTracking(CHANNELING, false);
    }

    private void spawnParticles(int amount) {
        int i = this.getColor();
        if (i != -1 && amount > 0) {
            double d = (double) (i >> 16 & 255) / 255.0;
            double e = (double) (i >> 8 & 255) / 255.0;
            double f = (double) (i & 255) / 255.0;

            for (int j = 0; j < amount; ++j) {
                this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
            }
        }
    }

    public int getColor() {
        return this.dataTracker.get(COLOR);
    }

    private void setColor(int color) {
        this.colorSet = true;
        this.dataTracker.set(COLOR, color);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) {
                    this.spawnParticles(1);
                    ParticleUtil.spawnParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.1, 1);
                }
            } else {
                this.spawnParticles(2);
                ParticleUtil.spawnParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.15, 1);
            }
        } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte) 0);
            this.potion = Potions.EMPTY;
            this.effects.clear();
            this.dataTracker.set(COLOR, -1);
        }
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        Entity entity = this.getEffectCause();
        Iterator<StatusEffectInstance> var3 = this.potion.getEffects().iterator();

        StatusEffectInstance statusEffectInstance;
        while (var3.hasNext()) {
            statusEffectInstance = var3.next();
            target.addStatusEffect(new StatusEffectInstance(statusEffectInstance.getEffectType(), Math.max(statusEffectInstance.mapDuration((i) -> i / 8), 1), statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()), entity);
        }

        if (!this.effects.isEmpty()) {
            var3 = this.effects.iterator();

            while (var3.hasNext()) {
                statusEffectInstance = var3.next();
                target.addStatusEffect(statusEffectInstance, entity);
            }
        }

        STATUS_EFFECTS.forEach((key, value) ->
                target.addStatusEffect(new StatusEffectInstance(key, STATUS_DURATION, value)));
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
        if (nbt.contains("Potion", 8)) {
            this.potion = PotionUtil.getPotion(nbt);
        }

        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(nbt)) {
            this.addEffect(statusEffectInstance);
        }

        if (nbt.contains("Color", 99)) {
            this.setColor(nbt.getInt("Color"));
        } else {
            this.initColor();
        }
        if (nbt.contains("channeling", NbtElement.BYTE_TYPE)) this.setChanneling(nbt.getBoolean("channeling"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.potion != Potions.EMPTY) {
            nbt.putString("Potion", Registries.POTION.getId(this.potion).toString());
        }

        if (this.colorSet) {
            nbt.putInt("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            NbtList nbtList = new NbtList();

            for (StatusEffectInstance statusEffectInstance : this.effects) {
                nbtList.add(statusEffectInstance.writeNbt(new NbtCompound()));
            }

            nbt.put("custom_potion_effects", nbtList);
        }
        nbt.putBoolean("channeling", this.getChanneling());
    }

    protected ItemStack asItemStack() {
        ItemStack itemStack = super.asItemStack();
        if (!this.effects.isEmpty() || this.potion != Potions.EMPTY) {
            PotionUtil.setPotion(itemStack, this.potion);
            PotionUtil.setCustomPotionEffects(itemStack, this.effects);
            if (this.colorSet) {
                itemStack.getOrCreateNbt().putInt("CustomPotionColor", this.getColor());
            }

        }
        return itemStack;
    }

    public void handleStatus(byte status) {
        if (status == 0) {
            int i = this.getColor();
            if (i != -1) {
                double d = (double) (i >> 16 & 255) / 255.0;
                double e = (double) (i >> 8 & 255) / 255.0;
                double f = (double) (i & 255) / 255.0;

                for (int j = 0; j < 20; ++j) {
                    this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
                }
            }
        } else if (status == 3) {
            ParticleUtil.spawnParticlesWithOffset(this.getWorld(), ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 0, 0, 20);
            ParticleUtil.spawnParticlesWithOffset(this.getWorld(), ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, 0.3, 30);
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
