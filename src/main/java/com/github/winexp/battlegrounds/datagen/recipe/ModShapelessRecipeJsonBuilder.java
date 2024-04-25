package com.github.winexp.battlegrounds.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

public class ModShapelessRecipeJsonBuilder extends ShapelessRecipeJsonBuilder {
    public ModShapelessRecipeJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
        super(category, output, count);
    }

    public static ModShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output) {
        return new ModShapelessRecipeJsonBuilder(category, output, 1);
    }

    public static ModShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
        return new ModShapelessRecipeJsonBuilder(category, output, count);
    }

    @Override
    public ModShapelessRecipeJsonBuilder input(TagKey<Item> tag) {
        return this.input(tag, 1);
    }

    public ModShapelessRecipeJsonBuilder input(TagKey<Item> tag, int count) {
        super.input(Ingredient.fromTag(tag), count);
        this.criterion("has_" + tag.id().getPath(), FabricRecipeProvider.conditionsFromTag(tag));
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder input(ItemConvertible itemProvider) {
        super.input(itemProvider);
        this.criterion(FabricRecipeProvider.hasItem(itemProvider), FabricRecipeProvider.conditionsFromItem(itemProvider));
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder input(ItemConvertible itemProvider, int size) {
        super.input(itemProvider, size);
        this.criterion(FabricRecipeProvider.hasItem(itemProvider), FabricRecipeProvider.conditionsFromItem(itemProvider));
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder input(Ingredient ingredient) {
        super.input(ingredient);
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder input(Ingredient ingredient, int size) {
        super.input(ingredient, size);
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        super.criterion(string, advancementCriterion);
        return this;
    }

    @Override
    public ModShapelessRecipeJsonBuilder group(@Nullable String string) {
        super.group(string);
        return this;
    }
}
