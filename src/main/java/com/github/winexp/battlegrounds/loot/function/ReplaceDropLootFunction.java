package com.github.winexp.battlegrounds.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;

import java.util.List;

public class ReplaceDropLootFunction extends ConditionalLootFunction {
    public static final Codec<ReplaceDropLootFunction> CODEC = RecordCodecBuilder.create((instance) ->
            addConditionsField(instance).and(Registries.ITEM.getCodec().fieldOf("dropItem").forGetter((function) ->
                    function.dropItem.asItem())).apply(instance, ReplaceDropLootFunction::new));

    private final ItemConvertible dropItem;

    private ReplaceDropLootFunction(List<LootCondition> conditions, ItemConvertible dropItem) {
        super(conditions);
        this.dropItem = dropItem;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        return dropItem.asItem().getDefaultStack();
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.REPLACE_DROP;
    }

    public static ConditionalLootFunction.Builder<?> builder(ItemConvertible dropItem) {
        return builder((conditions) -> new ReplaceDropLootFunction(conditions, dropItem));
    }
}
