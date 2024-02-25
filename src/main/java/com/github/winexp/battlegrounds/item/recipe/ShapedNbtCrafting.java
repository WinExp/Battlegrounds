package com.github.winexp.battlegrounds.item.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class ShapedNbtCrafting implements NbtCrafting {
    private final ItemStack result;
    private final ShapedRecipe recipe;
    private final Identifier identifier;

    public ShapedNbtCrafting(Identifier identifier,
                             RawShapedRecipe raw,
                             CraftingRecipeCategory category,
                             Supplier<ItemStack> result) {
        this(identifier, raw, category, result.get());
    }

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
    public ItemStack getItemStack() {
        return result;
    }

    @Override
    public CraftingRecipe getRecipe() {
        return recipe;
    }
}
