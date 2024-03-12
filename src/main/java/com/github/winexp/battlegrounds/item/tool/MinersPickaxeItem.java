package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.NbtCrafting;
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

public class MinersPickaxeItem extends PickaxeItem implements NbtCrafting, EnchantRestrict {
    public static final Identifier IDENTIFIER = new Identifier("battlegrounds", "miners_pickaxe");
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FORTUNE, 3,
            Enchantments.EFFICIENCY, 5,
            Enchantments.SMELTING, 1,
            Enchantments.MENDING, 1,
            Enchantments.UNBREAKING, 3,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 2
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
                        'b', Ingredient.ofItems(Items.IRON_PICKAXE)
                ),
                "aaa",
                "aba",
                "aaa");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
