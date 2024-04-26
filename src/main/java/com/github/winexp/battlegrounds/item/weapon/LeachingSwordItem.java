package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class LeachingSwordItem extends SwordItem {
    public LeachingSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
