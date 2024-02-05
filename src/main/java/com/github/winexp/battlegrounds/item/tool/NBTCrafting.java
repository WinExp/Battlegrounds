package com.github.winexp.battlegrounds.item.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;

public interface NBTCrafting {
    ItemStack getItemStack();
    ShapedRecipe getRecipe();
}
