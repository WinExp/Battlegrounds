package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.util.Identifier;

public interface NBTCrafting {
    Identifier getIdentifier();
    ItemStack getItemStack();
    CraftingRecipe getRecipe();
}
