package com.github.winexp.battlegrounds.loot.function;

import com.mojang.serialization.Codec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootFunctionTypes extends net.minecraft.loot.function.LootFunctionTypes {
    public static final LootFunctionType REPLACE_ITEM = register(new Identifier("battlegrounds", "replace_item"), ReplaceItemLootFunction.CODEC);

    private static LootFunctionType register(Identifier id, Codec<? extends LootFunction> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, id, new LootFunctionType(codec));
    }

    public static void registerLootFunctions() {
    }
}
