package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class LeachingSwordItem extends SwordItem {
    public LeachingSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    /*
    return new ShapedItemNbtRecipe(
                RawShapedRecipe.create(Map.of(
                                'a', Ingredient.ofItems(Items.DIAMOND),
                                'b', Ingredient.ofItems(Items.COBWEB),
                                'c', Ingredient.ofItems(Items.SPIDER_EYE),
                                'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                                'e', Ingredient.ofItems(Items.DIAMOND_BLOCK)
                        ),
                        "abe",
                        "cdc",
                        "aba"
                ),
                CraftingRecipeCategory.EQUIPMENT,
                this.getDefaultStack()
        ).getRecipe();
    */
}
