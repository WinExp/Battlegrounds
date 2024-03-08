package com.github.winexp.battlegrounds.enchantment;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class SmeltingEnchantment extends Enchantment {
    public final LootCondition.Builder DEFAULT_SMELTABLE_CONDITION = MatchToolLootCondition.builder(ItemPredicate.Builder.create()
                    .enchantment(new EnchantmentPredicate(this, NumberRange.IntRange.atLeast(1))));
    public final ConditionalLootFunction.Builder<?> DEFAULT_SMELTABLE_FUNCTION = FurnaceSmeltLootFunction.builder();
    private final HashMap<Identifier, ConditionalLootFunction.Builder<?>> smeltableBlocks = new HashMap<>();

    public SmeltingEnchantment() {
        this(Rarity.COMMON, EnchantmentTarget.DIGGER, EquipmentSlot.MAINHAND);
    }

    protected SmeltingEnchantment(Rarity rarity, EnchantmentTarget target, EquipmentSlot... slots) {
        super(rarity, target, slots);
    }

    public boolean isSmeltable(Identifier lootTableId) {
        return this.smeltableBlocks.keySet().stream().anyMatch(lootTableId::equals);
    }

    public ConditionalLootFunction.Builder<?> getSmeltableFunction(Identifier lootTableId) {
        return this.smeltableBlocks.get(lootTableId);
    }

    public void registerSmeltable(Block... blocks) {
        for (Block block : blocks) {
            this.registerSmeltable(block, DEFAULT_SMELTABLE_FUNCTION);
        }
    }

    public void registerSmeltable(Block block, ConditionalLootFunction.Builder<?> function) {
        this.smeltableBlocks.put(block.getLootTableId(), function);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.SILK_TOUCH;
    }

    @Override
    public int getMinPower(int level) {
        return 5;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }
}
