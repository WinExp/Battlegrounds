package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.entity.effect.StatusEffects;
import com.github.winexp.battlegrounds.item.EnchantRestrict;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtCrafting;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PVPProSwordItem extends LegendarySwordItem implements ItemNbtCrafting, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 3,
            Enchantments.SWEEPING, 2,
            Enchantments.LOOTING, 3
    );
    private static final Map<StatusEffect, Integer> enrichEffects = Map.of(
            StatusEffects.FIRE_RESISTANCE, 0,
            StatusEffects.STRENGTH, 0
    );

    public PVPProSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.1")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        tooltip.add(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.2")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        tooltip.add(Text.translatable("item.battlegrounds.pvp_pro_sword.tooltip.3")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull Map<StatusEffect, Integer> getEnrichEffects() {
        return enrichEffects;
    }

    @Override
    public @NotNull Map<StatusEffect, Integer> getAttackEffects() {
        return Map.of();
    }

    @Override
    public @NotNull Multimap<EntityAttribute, EntityAttributeModifier> getCustomModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
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
                        'b', Ingredient.ofItems(Items.GOLD_BLOCK),
                        'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE),
                        'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                        'e', Ingredient.ofItems(Items.DIAMOND_BLOCK)
                ),
                "aba",
                "cdc",
                "ebe");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
