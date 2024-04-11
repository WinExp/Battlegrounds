package com.github.winexp.battlegrounds.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;

public interface EnchantRestrict {
    default boolean isEnchantable(Enchantment enchantment, EnchantmentTarget target) {
        return false;
    }
    default boolean isGrindable(ItemStack stack) {
        return false;
    }
}
