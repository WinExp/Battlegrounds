package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.tag.ItemTags;

public class ChannelingProEnchantment extends Enchantment {
    public ChannelingProEnchantment() {
        this(Enchantment.properties(
                ItemTags.BOW_ENCHANTABLE,
                0, 1,
                Enchantment.leveledCost(12, 13),
                Enchantment.leveledCost(25, 13), 1
        ));
    }

    protected ChannelingProEnchantment(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}
