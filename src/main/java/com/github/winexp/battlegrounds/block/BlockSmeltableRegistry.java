package com.github.winexp.battlegrounds.block;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.function.ReplaceDropLootFunction;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class BlockSmeltableRegistry {
    private static final LootCondition.Builder DEFAULT_SMELTABLE_CONDITION = MatchToolLootCondition.builder(ItemPredicate.Builder.create()
            .enchantment(new EnchantmentPredicate(Enchantments.SMELTING, NumberRange.IntRange.atLeast(1))));
    public static final ConditionalLootFunction.Builder<?> DEFAULT_SMELTABLE_FUNCTION = FurnaceSmeltLootFunction.builder();
    private static final HashMap<Identifier, ConditionalLootFunction.Builder<?>> smeltableBlocks = new HashMap<>();

    public static boolean isSmeltable(Identifier lootTableId) {
        return smeltableBlocks.containsKey(lootTableId);
    }

    public static ConditionalLootFunction.Builder<?> getLootFunction(Identifier lootTableId) {
        return smeltableBlocks.get(lootTableId);
    }

    public static void registerDefaults() {
        register(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        register(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
        register(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        register(Blocks.NETHER_GOLD_ORE, ReplaceDropLootFunction.builder(Items.GOLD_INGOT));
    }

    public static void register(Block... blocks) {
        for (Block block : blocks) {
            register(block, DEFAULT_SMELTABLE_FUNCTION);
        }
    }

    public static void register(Block block, ConditionalLootFunction.Builder<?> lootFunction) {
        register(block.getLootTableId(), lootFunction);
    }

    public static void register(Identifier lootTableId, ConditionalLootFunction.Builder<?> lootFunction) {
        smeltableBlocks.put(lootTableId, lootFunction.conditionally(DEFAULT_SMELTABLE_CONDITION));
    }
}
