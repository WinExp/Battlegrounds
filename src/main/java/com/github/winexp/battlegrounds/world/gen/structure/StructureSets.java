package com.github.winexp.battlegrounds.world.gen.structure;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.structure.Structure;

public class StructureSets implements net.minecraft.structure.StructureSets {
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

    private static int getSalt(RegistryKey<?> structureSetKey) {
        return Math.abs(structureSetKey.getValue().hashCode());
    }

    private static void registerDefault(Registerable<StructureSet> structureSetRegisterable,
                                        RegistryKey<StructureSet> structureSetKey,
                                        RegistryKey<Structure> structureKey,
                                        int spacing, int separation) {
        RegistryEntryLookup<Structure> structureRegistryEntryLookup = structureSetRegisterable.getRegistryLookup(RegistryKeys.STRUCTURE);
        structureSetRegisterable.register(structureSetKey, new StructureSet(structureRegistryEntryLookup.getOrThrow(structureKey),
                new RandomSpreadStructurePlacement(spacing, separation, SpreadType.LINEAR, getSalt(structureSetKey))));
    }

    public static void register(Registerable<StructureSet> structureSetRegisterable) {
        registerDefault(structureSetRegisterable, StructureSets.ANCIENT_RUINS, Structures.ANCIENT_RUINS, 15, 4);
        registerDefault(structureSetRegisterable, StructureSets.CANOPIES, Structures.CANOPIES, 26, 7);
        registerDefault(structureSetRegisterable, StructureSets.DESERT_HUT, Structures.DESERT_HUT, 28, 7);
        registerDefault(structureSetRegisterable, StructureSets.IZAKAYA, Structures.IZAKAYA, 32, 10);
        registerDefault(structureSetRegisterable, StructureSets.KIOSK, Structures.KIOSK, 30, 8);
        registerDefault(structureSetRegisterable, StructureSets.LIBRARY, Structures.LIBRARY, 25, 6);
        registerDefault(structureSetRegisterable, StructureSets.MEDIEVAL_FORTRESS, Structures.MEDIEVAL_FORTRESS, 26, 8);
        registerDefault(structureSetRegisterable, StructureSets.MEDIEVAL_LIBRARY, Structures.MEDIEVAL_LIBRARY, 34, 12);
        registerDefault(structureSetRegisterable, StructureSets.RELIC_OF_FANTASY, Structures.RELIC_OF_FANTASY, 23, 5);
        registerDefault(structureSetRegisterable, StructureSets.WINE_SHOP, Structures.WINE_SHOP, 27, 8);
    }
}
