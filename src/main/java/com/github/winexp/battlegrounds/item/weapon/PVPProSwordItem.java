package com.github.winexp.battlegrounds.item.weapon;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.item.recipe.ItemNbtRecipe;
import com.github.winexp.battlegrounds.item.recipe.ShapedItemNbtRecipe;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
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

public class PVPProSwordItem extends LegendarySwordItem implements ItemNbtRecipe {
    private static final Map<Enchantment, Integer> ENCHANTMENTS = Map.of(
            Enchantments.FIRE_ASPECT, 2,
            Enchantments.KNOCKBACK, 2,
            Enchantments.SWEEPING, 2,
            Enchantments.LOOTING, 3
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
                                'b', Ingredient.ofItems(Items.GOLD_BLOCK),
                                'c', Ingredient.ofItems(Items.TOTEM_OF_UNDYING, Items.GOLDEN_APPLE),
                                'd', Ingredient.ofItems(Items.DIAMOND_SWORD),
                                'e', Ingredient.ofItems(Items.DIAMOND_BLOCK)
                        ),
                        "aba",
                        "cdc",
                        "ebe"
                ),
                CraftingRecipeCategory.EQUIPMENT,
                this.getDefaultStack()
        ).getRecipe();
    }
}
