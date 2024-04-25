package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SevenElevenSwordItem extends LegendarySwordItem {
    public SevenElevenSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.1")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
        tooltip.add(Text.translatable("item.battlegrounds.seven_eleven_sword.tooltip.2")
                .formatted(Formatting.GOLD)
                .styled(style -> style.withBold(true)));
    }

    /*
    return new ShapedItemNbtRecipe(
                RawShapedRecipe.create(
                        Map.of(
                                'a', Ingredient.ofItems(Items.DIAMOND),
                                'b', Ingredient.ofItems(Items.AMETHYST_SHARD),
                                'c', Ingredient.ofStacks(PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.TURTLE_MASTER)),
                                'd', Ingredient.ofItems(Items.NETHERITE_SWORD),
                                'e', Ingredient.ofStacks(PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRONG_SWIFTNESS)),
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
    */
}
