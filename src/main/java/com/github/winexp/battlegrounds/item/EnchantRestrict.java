package com.github.winexp.battlegrounds.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;

public interface EnchantRestrict {
    default boolean isEnchantable(Enchantment enchantment, EnchantmentTarget target) {
        return false;
    }
    default boolean isGrindable() {
        return false;
    }
}
