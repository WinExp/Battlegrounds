package com.github.winexp.battlegrounds.item;

public interface EnchantRestrict {
    default boolean isAnvilEnchantable() {
        return false;
    }
    default boolean isGrindable() {
        return false;
    }
}
