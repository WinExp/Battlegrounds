package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.item.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public class PVPProSwordItem extends SwordItem {
    public final static float DAMAGE_BONUS = 2.5F;
    public final static Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 1,
            Enchantments.KNOCKBACK, 1,
            Enchantments.SWEEPING, 2,
            Enchantments.LOOTING, 3
            );

    public PVPProSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings){
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static void addEffects(ServerPlayerEntity player){
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2, 0));
    }

    public ItemStack getItemStack(){
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach((stack::addEnchantment));
        return stack;
    }

    public static ShapedRecipe getRecipe(){
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(net.minecraft.item.Items.DIAMOND),
                        'b', Ingredient.ofItems(net.minecraft.item.Items.STICK)
                ),
                "  a",
                " a ",
                "b  ");
        ShapedRecipe shaped = new ShapedRecipe("battlegrounds.pvp_pro_sword",
                CraftingRecipeCategory.MISC,
                rawShaped,
                Items.PVP_PRO_SWORD.getItemStack()
        );
        return shaped;
    }
}
