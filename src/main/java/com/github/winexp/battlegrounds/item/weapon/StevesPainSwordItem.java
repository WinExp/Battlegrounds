package com.github.winexp.battlegrounds.item.weapon;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;

import java.util.List;

public class StevesPainSwordItem extends LegendarySwordItem {
    public StevesPainSwordItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    // TODO: 史蒂夫の痛 物品提示
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }
}
