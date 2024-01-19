package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ChannelingProEnchantment extends Enchantment {
    public ChannelingProEnchantment() {
        this(Rarity.VERY_RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);
    }

    protected ChannelingProEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots){
        super(rarity, target, slots);
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
