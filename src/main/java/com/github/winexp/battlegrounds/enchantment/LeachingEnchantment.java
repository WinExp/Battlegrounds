package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class LeachingEnchantment extends Enchantment {
    public static final int DURATION_PER_LEVEL = 5 * 20;

    public LeachingEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND);
    }

    protected LeachingEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots) {
        super(rarity, target, slots);
    }

    private void giveEffects(LivingEntity source, LivingEntity target, int level) {
        int duration = Math.min(DURATION_PER_LEVEL * level, 30 * 20);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, duration, 3), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, duration, 2), source);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 0), source);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity livingEntity) {
            giveEffects(user, livingEntity, level);
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
