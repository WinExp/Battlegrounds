package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import com.github.winexp.battlegrounds.item.recipe.ShapedItemNbtRecipe;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MyHolySwordItem extends LegendarySwordItem implements ItemNbtRecipe {
    private static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.SHARPNESS, 5,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 2,
            Enchantments.SWEEPING, 2
    );

    public MyHolySwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // TODO: 我滴圣剑 Tooltip
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public Recipe<?> getRecipe() {
        return new ShapedItemNbtRecipe(
                RawShapedRecipe.create(
                        Map.of(
                                'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                'b', Ingredient.ofItems(Items.ADVANCED_PRECISION_CORE),
                                'c', Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                                'd', Ingredient.ofItems(Items.NETHERITE_SWORD)
                        ),
                        "aba",
                        "cdc",
                        "aba"
                ),
                CraftingRecipeCategory.EQUIPMENT,
                this.getDefaultStack()
        ).getRecipe();
    }
}
