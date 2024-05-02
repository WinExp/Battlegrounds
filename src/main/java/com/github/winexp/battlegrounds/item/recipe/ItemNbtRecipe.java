package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface ItemNbtRecipe extends NbtRecipe {
    @Override
    default Identifier getIdentifier() {
        return Registries.ITEM.getId(this.getDefaultStack().getItem());
    }
}
