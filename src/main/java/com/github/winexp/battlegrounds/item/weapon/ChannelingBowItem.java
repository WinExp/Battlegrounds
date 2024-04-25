package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.item.EnchantRestrict;
import net.minecraft.item.BowItem;

public class ChannelingBowItem extends BowItem implements EnchantRestrict {
    public static final int DURABILITY = 500;

    public ChannelingBowItem(Settings settings) {
        super(settings);
    }

    /*
    RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                        'b', Ingredient.ofItems(Items.DIAMOND),
                        'c', Ingredient.ofItems(Items.ANCIENT_DEBRIS),
                        'd', Ingredient.ofItems(Items.BOW),
                        'e', Ingredient.ofItems(Items.LIGHTNING_ROD),
                        'f', Ingredient.ofItems(Items.TOTEM_OF_UNDYING),
                        'g', Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE)
                ),
                "afa",
                "cde",
                "agb");
    */
}
