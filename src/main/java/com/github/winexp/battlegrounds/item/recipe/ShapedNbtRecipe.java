package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

public class ShapedNbtRecipe implements NbtRecipe {
    private final ItemStack result;
    private final ShapedRecipe recipe;
    private final Identifier identifier;

    public ShapedNbtRecipe(Identifier identifier,
                           RawShapedRecipe raw,
                           CraftingRecipeCategory category,
                           ItemStack result) {
        this.identifier = identifier;
        this.result = result;
        this.recipe = new ShapedRecipe(identifier.toString(), category, raw, result);
    }

    @Override
    public Identifier getIdentifier() {
        return this.identifier;
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
