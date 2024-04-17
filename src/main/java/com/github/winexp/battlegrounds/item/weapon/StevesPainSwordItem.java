package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public class StevesPainSwordItem extends LegendarySwordItem implements ItemNbtCrafting, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.STEVES_PAIN, 1,
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.MENDING, 1
    );

    public StevesPainSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (player.getEquippedStack(EquipmentSlot.MAINHAND).isOf(this)
                    || player.getEquippedStack(EquipmentSlot.OFFHAND).isOf(this)) {
                if (server.getTicks() % 50 == 0) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 50, 0), player);
                }
            }
        }
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
                        'b', Ingredient.ofItems(Items.GHAST_TEAR),
                        'c', Ingredient.ofItems(Items.GOLDEN_APPLE),
                        'd', Ingredient.ofItems(Items.LEACHING_SWORD),
                        'e', Ingredient.ofItems(Items.ENCHANTED_GOLDEN_APPLE),
                        'f', Ingredient.ofItems(Items.BLAZE_POWDER)
                ),
                "aba",
                "cde",
                "afa");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
