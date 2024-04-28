package com.github.winexp.battlegrounds.item.mining;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class MinersPickaxeItem extends PickaxeItem implements EnchantRestrict {
    public MinersPickaxeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
