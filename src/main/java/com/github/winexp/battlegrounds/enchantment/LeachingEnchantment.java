package com.github.winexp.battlegrounds.enchantment;

import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Map;

public class LeachingEnchantment extends Enchantment {
    public static final int DURATION_PER_LEVEL = 5 * 20;
    private static final Map<StatusEffect, Integer> attackEffects = Map.of(
            StatusEffects.POISON, 3,
            StatusEffects.SLOWNESS, 0,
            StatusEffects.HUNGER, 2,
            StatusEffects.NAUSEA, 0
    );

    public LeachingEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND);
    }

    protected LeachingEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots) {
        super(rarity, target, slots);
    }

    private void giveEffects(LivingEntity source, LivingEntity target, int level) {
        int duration = Math.min(DURATION_PER_LEVEL * level, 30 * 20);
        attackEffects.forEach((effect, amplifier) ->
                target.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier), source));
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (target instanceof LivingEntity livingEntity) {
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
