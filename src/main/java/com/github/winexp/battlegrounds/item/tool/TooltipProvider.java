package com.github.winexp.battlegrounds.item.tool;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@FunctionalInterface
public interface TooltipProvider {
    Text getTooltip(ItemStack stack, World world, TooltipContext context);
}
