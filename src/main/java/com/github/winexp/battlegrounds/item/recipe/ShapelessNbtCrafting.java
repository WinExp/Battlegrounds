package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ShapelessNbtCrafting implements NbtCrafting {
    private final ItemStack result;
    private final ShapelessRecipe recipe;
    private final Identifier identifier;

    public ShapelessNbtCrafting(Identifier identifier,
                                CraftingRecipeCategory category,
                                ItemStack result,
                                List<Ingredient> ingredients) {
        this.identifier = identifier;
        this.result = result;
        this.recipe = new ShapelessRecipe(identifier.toString(), category, result,
                DefaultedList.copyOf(null, ingredients.toArray(new Ingredient[0])));
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
