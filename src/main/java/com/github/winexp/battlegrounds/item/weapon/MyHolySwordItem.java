package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;

import java.util.List;

public class MyHolySwordItem extends LegendarySwordItem {
    public MyHolySwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // TODO: 我滴圣剑 Tooltip
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    /*
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
    */
}
