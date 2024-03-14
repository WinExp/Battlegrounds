package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

public class ShapedNbtCrafting implements CustomNbtCrafting {
    private final ItemStack result;
    private final ShapedRecipe recipe;
    private final Identifier identifier;

    public ShapedNbtCrafting(Identifier identifier,
                             RawShapedRecipe raw,
                             CraftingRecipeCategory category,
                             ItemStack result) {
        this.identifier = identifier;
        this.result = result;
        this.recipe = new ShapedRecipe(identifier.toString(), category, raw, result);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public ItemStack getDefaultStack() {
        return result;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return recipe;
    }
}
