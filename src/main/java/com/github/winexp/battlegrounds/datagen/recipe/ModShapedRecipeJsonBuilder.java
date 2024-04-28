package com.github.winexp.battlegrounds.datagen.recipe;

import com.github.winexp.battlegrounds.mixin.ShapedRecipeAccessor;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.ComponentChanges;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModShapedRecipeJsonBuilder extends ShapedRecipeJsonBuilder {
    private ComponentChanges componentChanges = ComponentChanges.EMPTY;

    public ModShapedRecipeJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
        super(category, output, count);
    }

    public static ModShapedRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output) {
        return create(category, output, 1);
    }

    public static ModShapedRecipeJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
        return new ModShapedRecipeJsonBuilder(category, output, count);
    }

    public ModShapedRecipeJsonBuilder componentChanges(ComponentChanges componentChanges) {
        this.componentChanges = componentChanges;
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder input(Character c, TagKey<Item> tag) {
        super.input(c, tag);
        this.criterion("has_" + tag.id().getPath(), FabricRecipeProvider.conditionsFromTag(tag));
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder input(Character c, ItemConvertible itemProvider) {
        super.input(c, Ingredient.ofItems(itemProvider));
        this.criterion(FabricRecipeProvider.hasItem(itemProvider), FabricRecipeProvider.conditionsFromItem(itemProvider));
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder input(Character c, Ingredient ingredient) {
        super.input(c, ingredient);
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder pattern(String patternStr) {
        super.pattern(patternStr);
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        super.criterion(string, advancementCriterion);
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder group(@Nullable String string) {
        super.group(string);
        return this;
    }

    @Override
    public ModShapedRecipeJsonBuilder showNotification(boolean showNotification) {
        super.showNotification(showNotification);
        return this;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        RecipeExporter modifiedExporter = new RecipeExporter() {
            @Override
            public void accept(Identifier recipeId, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
                ShapedRecipeAccessor accessor = (ShapedRecipeAccessor) shapedRecipe;
                shapedRecipe.getResult(null).applyChanges(ModShapedRecipeJsonBuilder.this.componentChanges);
                shapedRecipe = new ShapedRecipe(shapedRecipe.getGroup(), shapedRecipe.getCategory(), accessor.getRaw(), shapedRecipe.getResult(null), shapedRecipe.showNotification());
                exporter.accept(recipeId, shapedRecipe, advancement);
            }

            @Override
            public Advancement.Builder getAdvancementBuilder() {
                return exporter.getAdvancementBuilder();
            }
        };
        super.offerTo(modifiedExporter, recipeId);
    }
}
