package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public class ShapedItemNbtRecipe implements ItemNbtRecipe {
    private final ItemStack result;
    private final ShapedRecipe recipe;

    public ShapedItemNbtRecipe(RawShapedRecipe raw,
                               CraftingRecipeCategory category,
                               ItemStack result) {
        this.result = result;
        this.recipe = new ShapedRecipe(this.getIdentifier().toString(), category, raw, result);
    }

    @Override
    public ItemStack getDefaultStack() {
        return this.result;
    }

    @Override
    public Recipe<?> getRecipe() {
        return this.recipe;
    }
}
