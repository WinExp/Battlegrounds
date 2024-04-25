package com.github.winexp.battlegrounds.block;

import com.github.winexp.battlegrounds.enchantment.Enchantments;
import com.github.winexp.battlegrounds.item.Items;
import com.github.winexp.battlegrounds.loot.function.ReplaceItemLootFunction;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

public class BlockSmeltableRegistry {
    private static final LootCondition.Builder DEFAULT_SMELTABLE_CONDITION = MatchToolLootCondition.builder(ItemPredicate.Builder.create()
            .component(ComponentPredicate.builder()
                    .add(DataComponentTypes.ENCHANTMENTS, Util.make(
                            new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT), builder ->
                                    builder.add(Enchantments.SMELTING, 1)).build())
                    .build()));
    public static final ConditionalLootFunction.Builder<?> DEFAULT_SMELTABLE_FUNCTION = FurnaceSmeltLootFunction.builder();
    private static final Map<RegistryKey<LootTable>, ConditionalLootFunction.Builder<?>> smeltableBlocks = new HashMap<>();

    public static boolean isSmeltable(RegistryKey<LootTable> key) {
        return smeltableBlocks.containsKey(key);
    }

    public static ConditionalLootFunction.Builder<?> getLootFunction(RegistryKey<LootTable> key) {
        return smeltableBlocks.get(key);
    }

    public static void registerDefaults() {
        register(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE);
        register(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
        register(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE);
        register(Blocks.NETHER_GOLD_ORE, ReplaceItemLootFunction.builder(Items.GOLD_INGOT));
    }

    public static void register(Block... blocks) {
        for (Block block : blocks) {
            register(block, DEFAULT_SMELTABLE_FUNCTION);
        }
    }

    public static void register(Block block, ConditionalLootFunction.Builder<?> lootFunction) {
        register(block.getLootTableKey(), lootFunction);
    }

    public static void register(RegistryKey<LootTable> key, ConditionalLootFunction.Builder<?> lootFunction) {
        smeltableBlocks.put(key, lootFunction.conditionally(DEFAULT_SMELTABLE_CONDITION));
    }
}
