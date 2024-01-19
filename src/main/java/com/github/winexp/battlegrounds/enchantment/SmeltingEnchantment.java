package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class SmeltingEnchantment extends Enchantment {
    public SmeltingEnchantment() {
        this(Rarity.COMMON, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND);
    }

    protected SmeltingEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots){
        super(rarity, target, slots);
    }

    @Override
    protected boolean canAccept(Enchantment other){
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }

    @Override
    public int getMinPower(int level){
        return 5;
    }

    @Override
    public int getMaxPower(int level){
        return super.getMinPower(level) + 50;
    }
}
