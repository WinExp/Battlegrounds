package com.github.winexp.battlegrounds.loot.function;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootFunctionTypes extends net.minecraft.loot.function.LootFunctionTypes {
    public static final LootFunctionType<ReplaceItemLootFunction> REPLACE_ITEM = register(new Identifier("battlegrounds", "replace_item"), ReplaceItemLootFunction.CODEC);

    private static <T extends LootFunction> LootFunctionType<T> register(Identifier id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, id, new LootFunctionType<>(codec));
    }

    public static void registerLootFunctions() {
    }
}
