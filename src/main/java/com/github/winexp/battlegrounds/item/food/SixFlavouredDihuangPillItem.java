package com.github.winexp.battlegrounds.item.food;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class SixFlavouredDihuangPillItem extends Item {
    public SixFlavouredDihuangPillItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.battlegrounds.six_flavoured_dihuang_pill.tooltip")
                .formatted(Formatting.DARK_PURPLE)
                .styled(style -> style
                        .withBold(true)
                        .withItalic(true)
                )
        );
    }
}
