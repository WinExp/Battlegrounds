package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;

import java.util.Map;

public class ChannelingBowItem extends BowItem implements ItemNbtCrafting, EnchantRestrict {
    public static final int DURABILITY = 500;
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.CHANNELING_PRO, 1,
            Enchantments.POWER, 5,
            Enchantments.PUNCH, 3,
            Enchantments.FLAME, 1,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.SHARPNESS, 7,
            Enchantments.INFINITY, 1
    );

    public ChannelingBowItem(Settings settings) {
        super(settings);
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
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
