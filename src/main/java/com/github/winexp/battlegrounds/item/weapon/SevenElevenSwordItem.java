package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import com.github.winexp.battlegrounds.item.recipe.ShapedItemNbtRecipe;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SevenElevenSwordItem extends LegendarySwordItem implements ItemNbtRecipe {
    private static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 2,
            Enchantments.SWEEPING, 3,
            Enchantments.LOOTING, 3
    );

    public SevenElevenSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
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
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        ENCHANTMENTS.forEach(stack::addEnchantment);
        return stack;
    }

    @Override
    public Recipe<?> getRecipe() {
        return new ShapedItemNbtRecipe(
                RawShapedRecipe.create(
                        Map.of(
                                'a', Ingredient.ofItems(Items.DIAMOND),
                                'b', Ingredient.ofItems(Items.AMETHYST_SHARD),
                                'c', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.TURTLE_MASTER)),
                                'd', Ingredient.ofItems(Items.NETHERITE_SWORD),
                                'e', Ingredient.ofStacks(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), Potions.STRONG_SWIFTNESS)),
                                'f', Ingredient.ofItems(Items.DIAMOND_BLOCK),
                                'g', Ingredient.ofItems(Items.PRECISION_CORE)
                        ),
                        "aba",
                        "cde",
                        "fgf"
                ),
                CraftingRecipeCategory.EQUIPMENT,
                this.getDefaultStack()
        ).getRecipe();
    }
}
