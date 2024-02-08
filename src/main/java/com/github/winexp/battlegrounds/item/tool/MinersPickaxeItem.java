package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.NBTCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MinersPickaxeItem extends PickaxeItem implements NBTCrafting {
    public final static Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FORTUNE, 3,
            Enchantments.EFFICIENCY, 4,
            Enchantments.SMELTING, 1,
            Enchantments.MENDING, 1
    );

    public MinersPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier("battlegrounds", "miners_pickaxe");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach((stack::addEnchantment));
        return stack;
    }

    @Override
    public ShapedRecipe getRecipe() {
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.IRON_PICKAXE)
                ),
                "aaa",
                "aba",
                "aaa");
        return new ShapedRecipe("battlegrounds.miners_pickaxe",
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                Items.MINERS_PICKAXE.getItemStack()
        );
    }
}
