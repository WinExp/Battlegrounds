package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;

import java.util.List;

public class StevesPainSwordItem extends LegendarySwordItem {
    public StevesPainSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // TODO: 史蒂夫の痛 物品提示
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    /*
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
    */
}
