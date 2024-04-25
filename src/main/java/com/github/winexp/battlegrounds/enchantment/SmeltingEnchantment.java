package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.tag.ItemTags;

public class SmeltingEnchantment extends Enchantment {
    public SmeltingEnchantment() {
        this(Enchantment.properties(
                ItemTags.MINING_LOOT_ENCHANTABLE,
                0, 1,
                Enchantment.leveledCost(1, 6),
                Enchantment.leveledCost(15, 6), 1
        ));
    }

    protected SmeltingEnchantment(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }
}
