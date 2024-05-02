package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public interface NbtRecipe {
    Identifier getIdentifier();

    ItemStack getDefaultStack();

    Recipe<?> getRecipe();

    default RecipeEntry<Recipe<?>> toRecipeEntry() {
        return new RecipeEntry<>(
                this.getIdentifier(),
                this.getRecipe()
        );
    }
}
