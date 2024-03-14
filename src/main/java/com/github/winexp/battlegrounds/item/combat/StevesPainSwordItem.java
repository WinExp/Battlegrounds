package com.github.winexp.battlegrounds.item.combat;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

import java.util.Map;

public class StevesPainSwordItem extends SwordItem implements ItemNbtCrafting, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.STEVES_PAIN, 1,
            Enchantments.MENDING, 1
    );

    public StevesPainSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public ShapedRecipe getRecipe() {
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                        'b', Ingredient.ofItems(Items.GHAST_TEAR),
                        'c', Ingredient.ofItems(Items.GOLDEN_APPLE),
                        'd', Ingredient.ofItems(Items.NETHERITE_SWORD),
                        'e', Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                        'f', Ingredient.ofItems(Items.BLAZE_POWDER)
                ),
                "aba",
                "cde",
                "afa");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
