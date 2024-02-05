package com.github.winexp.battlegrounds.item.tool;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.util.Identifier;

import java.util.Map;

public class PVPProSwordItem extends SwordItem implements NBTCrafting {
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

    public void addEffects(ServerPlayerEntity player){
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 2, 0));
    }

    @Override
    public ItemStack getItemStack(){
        ItemStack stack = new ItemStack(this, 1);
        ENCHANTMENTS.forEach((stack::addEnchantment));
        return stack;
    }

    @Override
    public ShapedRecipe getRecipe(){
        RawShapedRecipe rawShaped = RawShapedRecipe.create(Map.of(
                        'a', Ingredient.ofItems(Items.IRON_INGOT),
                        'b', Ingredient.ofItems(Items.GOLD_INGOT),
                        'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE),
                        'd', Ingredient.ofItems(Items.DIAMOND_SWORD)
                ),
                "aba",
                "cdc",
                "aba");
        ShapedRecipe shaped = new ShapedRecipe("battlegrounds.pvp_pro_sword",
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                Items.PVP_PRO_SWORD.getItemStack()
        );
        return shaped;
    }
}
