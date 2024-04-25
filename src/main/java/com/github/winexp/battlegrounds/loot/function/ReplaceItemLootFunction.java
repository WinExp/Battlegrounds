package com.github.winexp.battlegrounds.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;

import java.util.List;

public class ReplaceItemLootFunction extends ConditionalLootFunction {
    public static final MapCodec<ReplaceItemLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            addConditionsField(instance).and(ItemStack.CODEC.fieldOf("item").forGetter(function ->
                    function.stack)).apply(instance, ReplaceItemLootFunction::new));

    private final ItemStack stack;

    private ReplaceItemLootFunction(List<LootCondition> conditions, ItemStack stack) {
        super(conditions);
        this.stack = stack;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        return this.stack;
    }

    @Override
    public LootFunctionType<ReplaceItemLootFunction> getType() {
        return LootFunctionTypes.REPLACE_ITEM;
    }

    public static ConditionalLootFunction.Builder<?> builder(ItemConvertible item) {
        return builder(item.asItem().getDefaultStack());
    }

    public static ConditionalLootFunction.Builder<?> builder(ItemStack stack) {
        return builder((conditions) -> new ReplaceItemLootFunction(conditions, stack));
    }
}
