package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Hand;

import java.util.Map;

public class KnockbackStickItem extends ToolItem implements ItemNbtCrafting, EnchantRestrict {
    private static final int DURATION = 5 * 20;
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.KNOCKBACK, 7,
            Enchantments.SHARPNESS, 1
    );

    public KnockbackStickItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    private void giveEffects(LivingEntity attacker, LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, DURATION, 2), attacker);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, DURATION, 0), attacker);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendToolBreakStatus(Hand.MAIN_HAND));
        this.giveEffects(attacker, target);
        return true;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public CraftingRecipe getRecipe() {
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.BLAZE_ROD),
                        'c', Ingredient.ofItems(Items.STICK)
                ),
                "aba",
                "bcb",
                "aba");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
