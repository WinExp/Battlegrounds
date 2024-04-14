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
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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

public class SevenElevenSwordItem extends LegendarySwordItem implements ItemNbtCrafting, EnchantRestrict {
    public static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 2,
            Enchantments.SWEEPING, 3,
            Enchantments.LOOTING, 3
    );
    private static final Map<StatusEffect, Integer> enrichEffects = Map.of(
            StatusEffects.JUMP_BOOST, 1,
            StatusEffects.SPEED, 1
    );
    private static final Map<StatusEffect, Integer> attackEffects = Map.of(
            StatusEffects.SLOWNESS, 1,
            StatusEffects.WEAKNESS, 0
    );

    public SevenElevenSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public @NotNull Map<StatusEffect, Integer> getEnrichEffects() {
        return enrichEffects;
    }

    @Override
    public int getAttackEffectsBound() {
        return 30;
    }

    @Override
    public @NotNull Map<StatusEffect, Integer> getAttackEffects() {
        return attackEffects;
    }

    @Override
    public @NotNull Multimap<EntityAttribute, EntityAttributeModifier> getCustomModifiers(EquipmentSlot slot) {
        return ImmutableMultimap.of();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.1")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.2")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
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
                        'a', Ingredient.ofItems(Items.DIAMOND),
                        'b', Ingredient.ofItems(Items.AMETHYST_SHARD),
                        'c', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.TURTLE_MASTER)),
                        'd', Ingredient.ofItems(Items.NETHERITE_SWORD),
                        'e', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.STRONG_SWIFTNESS)),
                        'f', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                        'g', Ingredient.ofItems(Items.EMERALD)
                ),
                "aba",
                "cde",
                "fgf");
        return new ShapedRecipe(this.getIdentifier().toString(),
                CraftingRecipeCategory.EQUIPMENT,
                rawShaped,
                this.getDefaultStack()
        );
    }
}
