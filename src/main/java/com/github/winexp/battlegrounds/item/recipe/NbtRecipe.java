package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

public interface NbtRecipe {
    NbtRecipe EMPTY = new NbtRecipe() {
        @Override
        public Identifier getIdentifier() {
            return null;
        }

        @Override
        public ItemStack getDefaultStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public CraftingRecipe getRecipe() {
            return null;
        }
    };

    Identifier getIdentifier();

    ItemStack getDefaultStack();

    CraftingRecipe getRecipe();

    default RecipeEntry<CraftingRecipe> toRecipeEntry() {
        return new RecipeEntry<>(
                this.getIdentifier(),
                this.getRecipe()
        );
    }
}
