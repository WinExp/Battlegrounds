package com.github.winexp.battlegrounds.util;

import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;

public class RecipeUtil {
    private static final HashMap<Identifier, RecipeEntry<? extends Recipe<?>>> recipeMap = new HashMap<>();

    public static Collection<RecipeEntry<?>> getRecipes() {
        return recipeMap.values();
    }

    public static RecipeEntry<?> getRecipe(Identifier id) {
        return recipeMap.get(id);
    }

    public static void addRecipe(NbtCrafting nbtCrafting) {
        addRecipe(nbtCrafting.toRecipeEntry());
    }

    public static <T extends Recipe<?>> void addRecipe(RecipeEntry<T> entry) {
        recipeMap.put(entry.id(), entry);
    }

    public static void removeRecipe(Identifier id) {
        recipeMap.remove(id);
    }

    public static void clearRecipes() {
        recipeMap.clear();
    }
}
