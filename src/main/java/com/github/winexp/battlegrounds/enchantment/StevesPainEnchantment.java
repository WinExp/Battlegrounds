package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.random.Random;

public class StevesPainEnchantment extends Enchantment {
    public static final int BOUND = 30;
    public static final int DURATION_PER_LEVEL = 5 * 20;

    public StevesPainEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND);
    }

    protected StevesPainEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots) {
        super(rarity, target, slots);
    }

    private void giveEffects(LivingEntity source, LivingEntity target, int level) {
        int duration = Math.min(DURATION_PER_LEVEL * level, 30 * 20);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 1), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, duration, 2), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, duration, 0), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 0), source);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        Random random = user.getRandom();
        if (random.nextInt(100) + 1 <= BOUND && target instanceof LivingEntity livingEntity) {
            this.giveEffects(user, livingEntity, level);
        }
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }
}
