package com.github.winexp.battlegrounds.item.mining;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;

import java.util.Map;

public class MinersPickaxeItem extends PickaxeItem implements ItemNbtRecipe, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FORTUNE, 2,
            Enchantments.EFFICIENCY, 4,
            Enchantments.SMELTING, 1,
            Enchantments.MENDING, 1,
            Enchantments.UNBREAKING, 3
    );

    public MinersPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
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
                        'a', Ingredient.ofItems(Items.RAW_IRON),
                        'b', Ingredient.fromTag(ItemTags.COALS),
                        'c', Ingredient.ofItems(Items.STICK)
                ),
                "aaa",
                "bcb",
                " c ");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
