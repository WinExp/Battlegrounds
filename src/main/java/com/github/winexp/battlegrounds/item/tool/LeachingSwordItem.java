package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Map;

public class LeachingSwordItem extends SwordItem implements NbtCrafting {
    public static final Identifier IDENTIFIER = new Identifier("battlegrounds", "leaching_sword");
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.LEACHING, 1,
            Enchantments.KNOCKBACK, 3
    );

    public LeachingSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public Identifier getIdentifier() {
        return IDENTIFIER;
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
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.COBWEB),
                        'c', Ingredient.ofItems(Items.SPIDER_EYE),
                        'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                        'e', Ingredient.ofItems(Items.DIAMOND_BLOCK)
                ),
                "abe",
                "cdc",
                "aba");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
