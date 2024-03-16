package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface ItemNbtCrafting extends NbtCrafting {
    @Override
    default Identifier getIdentifier() {
        return Registries.ITEM.getId((Item) this);
    }
}
