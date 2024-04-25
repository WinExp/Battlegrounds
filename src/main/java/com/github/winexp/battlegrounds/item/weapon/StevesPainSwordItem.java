package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import com.github.winexp.battlegrounds.item.recipe.ShapedItemNbtRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

import java.util.Map;

public class StevesPainSwordItem extends LegendarySwordItem implements ItemNbtRecipe {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.STEVES_PAIN, 1,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.MENDING, 1
    );

    public StevesPainSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public Recipe<?> getRecipe() {
        return new ShapedItemNbtRecipe(
                RawShapedRecipe.create(Map.of(
                                'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                'b', Ingredient.ofItems(Items.GHAST_TEAR),
                                'c', Ingredient.ofItems(Items.GOLDEN_APPLE),
                                'd', Ingredient.ofItems(Items.LEACHING_SWORD),
                                'e', Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                                'f', Ingredient.ofItems(Items.ADVANCED_PRECISION_CORE)
                        ),
                        "aba",
                        "cde",
                        "afa"
                ),
                CraftingRecipeCategory.EQUIPMENT,
                this.getDefaultStack()
        ).getRecipe();
    }
}
