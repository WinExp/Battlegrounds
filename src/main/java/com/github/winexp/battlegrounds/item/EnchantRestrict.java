package com.github.winexp.battlegrounds.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public interface EnchantRestrict {
    default boolean isEnchantable(Enchantment enchantment, Enchantment.Properties properties) {
        return false;
    }
    default boolean isGrindable(ItemStack stack) {
        return false;
    }
}
