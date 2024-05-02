package com.github.winexp.battlegrounds.item.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AdvancedPrecisionCoreItem extends Item {
    public AdvancedPrecisionCoreItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
