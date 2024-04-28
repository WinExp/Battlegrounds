package com.github.winexp.battlegrounds.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;

public class StructurePoolKeys {
    public static final RegistryKey<StructurePool> ANCIENT_RUINS = of("ancient_ruins");
    public static final RegistryKey<StructurePool> CANOPIES = of("canopies");
    public static final RegistryKey<StructurePool> DESERT_HUT = of("desert_hut");
    public static final RegistryKey<StructurePool> IZAKAYA = of("izakaya");
    public static final RegistryKey<StructurePool> KIOSK = of("kiosk");
    public static final RegistryKey<StructurePool> LIBRARY = of("library");
    public static final RegistryKey<StructurePool> MEDIEVAL_FORTRESS = of("medieval_fortress");
    public static final RegistryKey<StructurePool> MEDIEVAL_LIBRARY = of("medieval_library");
    public static final RegistryKey<StructurePool> RELIC_OF_FANTASY = of("relic_of_fantasy");
    public static final RegistryKey<StructurePool> WINE_SHOP = of("wine_shop");

    private static RegistryKey<StructurePool> of(String id) {
        return RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier("battlegrounds", id));
    }
}
