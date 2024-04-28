package com.github.winexp.battlegrounds.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class StructureKeys implements net.minecraft.world.gen.structure.StructureKeys {
    public static RegistryKey<Structure> ANCIENT_RUINS = of("ancient_ruins");
    public static RegistryKey<Structure> CANOPIES = of("canopies");
    public static RegistryKey<Structure> DESERT_HUT = of("desert_hut");
    public static RegistryKey<Structure> IZAKAYA = of("izakaya");
    public static RegistryKey<Structure> KIOSK = of("kiosk");
    public static RegistryKey<Structure> LIBRARY = of("library");
    public static RegistryKey<Structure> MEDIEVAL_FORTRESS = of("medieval_fortress");
    public static RegistryKey<Structure> MEDIEVAL_LIBRARY = of("medieval_library");
    public static RegistryKey<Structure> RELIC_OF_FANTASY = of("relic_of_fantasy");
    public static RegistryKey<Structure> WINE_SHOP = of("wine_shop");

    private static RegistryKey<Structure> of(String name) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier("battlegrounds", name));
    }
}
