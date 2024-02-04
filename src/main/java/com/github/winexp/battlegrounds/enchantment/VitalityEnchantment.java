package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

public class VitalityEnchantment extends Enchantment {
    public VitalityEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, EquipmentSlot.MAINHAND);
    }

    protected VitalityEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots){
        super(rarity, target, slots);
    }

    public static void giveEffects(ServerPlayerEntity player, int level){
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 2, level - 1));
    }

    @Override
    public int getMaxLevel(){
        return 3;
    }

    @Override
    public int getMinPower(int level){
        return 30;
    }

    @Override
    public int getMaxPower(int level){
        return super.getMaxPower(level) + 40;
    }
}
