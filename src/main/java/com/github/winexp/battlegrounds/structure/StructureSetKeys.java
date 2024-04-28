package com.github.winexp.battlegrounds.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;

public class StructureSetKeys implements net.minecraft.structure.StructureSetKeys {
    public static RegistryKey<StructureSet> ANCIENT_RUINS = of("ancient_ruins");
    public static RegistryKey<StructureSet> CANOPIES = of("canopies");
    public static RegistryKey<StructureSet> DESERT_HUT = of("desert_hut");
    public static RegistryKey<StructureSet> IZAKAYA = of("izakaya");
    public static RegistryKey<StructureSet> KIOSK = of("kiosk");
    public static RegistryKey<StructureSet> LIBRARY = of("library");
    public static RegistryKey<StructureSet> MEDIEVAL_FORTRESS = of("medieval_fortress");
    public static RegistryKey<StructureSet> MEDIEVAL_LIBRARY = of("medieval_library");
    public static RegistryKey<StructureSet> RELIC_OF_FANTASY = of("relic_of_fantasy");
    public static RegistryKey<StructureSet> WINE_SHOP = of("wine_shop");

    private static RegistryKey<StructureSet> of(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE_SET, new Identifier("battlegrounds", name));
    }
}
