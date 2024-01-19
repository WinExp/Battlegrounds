package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class LeachingEnchantment extends Enchantment {
    public final static int DURATION = 3 * 20;

    public LeachingEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, EquipmentSlot.MAINHAND);
    }

    protected LeachingEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots){
        super(rarity, target, slots);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level){
        if (target instanceof LivingEntity livingEntity){
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, DURATION, 1), user);
        }
    }

    @Override
    public int getMinPower(int level){
        return 30;
    }

    @Override
    public int getMaxPower(int level){
        return 60;
    }
}
