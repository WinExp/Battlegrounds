package com.github.winexp.battlegrounds.loot;

import com.github.winexp.battlegrounds.registry.RegistryKeys;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class LootTables extends net.minecraft.loot.LootTables {
    public static final RegistryKey<LootTable> METALS = of("chests/metals");
    public static final RegistryKey<LootTable> CROPS = of("chests/crops");
    public static final RegistryKey<LootTable> WINE_SHOP = of("chests/wine_shop");
    public static final RegistryKey<LootTable> EQUIPMENTS = of("chests/equipments");

    private static RegistryKey<LootTable> of(String name) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, new Identifier("battlegrounds", name));
    }
}
