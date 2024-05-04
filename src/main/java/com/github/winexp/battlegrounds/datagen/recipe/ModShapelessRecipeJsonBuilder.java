package com.github.winexp.battlegrounds.datagen.recipe;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModShapelessRecipeJsonBuilder extends ShapelessRecipeJsonBuilder {
    private final ComponentChanges.Builder componentChanges = ComponentChanges.builder();

    public ModShapelessRecipeJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
        super(category, output, count);
    }

    public static ModShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output) {
        return new ModShapelessRecipeJsonBuilder(category, output, 1);
    }

    public static ModShapelessRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
        return new ModShapelessRecipeJsonBuilder(category, output, count);
    }

    public <T> ModShapelessRecipeJsonBuilder component(DataComponentType<T> componentType, T component) {
        this.componentChanges.add(componentType, component);
        return this;
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

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        RecipeExporter modifiedExporter = new RecipeExporter() {
            @Override
            public void accept(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
                shapelessRecipe.getResult(null).applyChanges(ModShapelessRecipeJsonBuilder.this.componentChanges.build());
                exporter.accept(recipeId, shapelessRecipe, advancement);
            }

            @Override
            public Advancement.Builder getAdvancementBuilder() {
                return exporter.getAdvancementBuilder();
            }
        };
        super.offerTo(modifiedExporter, recipeId);
    }
}
