package com.github.winexp.battlegrounds.world.gen.structure;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.Map;

public class Structures extends net.minecraft.world.gen.structure.Structures {
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

    private static RegistryKey<Structure> of(String id) {
        return RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier("battlegrounds", id));
    }

    private static void registerDefault(Registerable<Structure> structureRegisterable,
                                        RegistryKey<Structure> structureKey,
                                        RegistryKey<StructurePool> structurePoolKey, int yOffset) {
        RegistryEntryLookup<Biome> biomeRegistryEntryLookup = structureRegisterable.getRegistryLookup(RegistryKeys.BIOME);
        RegistryEntryLookup<StructurePool> structurePoolRegistryEntryLookup = structureRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
        structureRegisterable.register(structureKey, new JigsawStructure(createConfig(
                biomeRegistryEntryLookup.getOrThrow(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE),
                Map.of(),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                StructureTerrainAdaptation.NONE
        ), structurePoolRegistryEntryLookup.getOrThrow(structurePoolKey), 1,
                ConstantHeightProvider.create(YOffset.fixed(yOffset)), false, Heightmap.Type.WORLD_SURFACE_WG));
    }

    public static void register(Registerable<Structure> structureRegisterable) {
        registerDefault(structureRegisterable, Structures.ANCIENT_RUINS, StructurePools.ANCIENT_RUINS, 1);
        registerDefault(structureRegisterable, Structures.CANOPIES, StructurePools.CANOPIES, 1);
        registerDefault(structureRegisterable, Structures.DESERT_HUT, StructurePools.DESERT_HUT, 0);
        registerDefault(structureRegisterable, Structures.IZAKAYA, StructurePools.IZAKAYA, -1);
        registerDefault(structureRegisterable, Structures.KIOSK, StructurePools.KIOSK, -2);
        registerDefault(structureRegisterable, Structures.LIBRARY, StructurePools.LIBRARY, 1);
        registerDefault(structureRegisterable, Structures.MEDIEVAL_FORTRESS, StructurePools.MEDIEVAL_FORTRESS, 0);
        registerDefault(structureRegisterable, Structures.MEDIEVAL_LIBRARY, StructurePools.MEDIEVAL_LIBRARY, 1);
        registerDefault(structureRegisterable, Structures.RELIC_OF_FANTASY, StructurePools.RELIC_OF_FANTASY, 0);
        registerDefault(structureRegisterable, Structures.WINE_SHOP, StructurePools.WINE_SHOP, 1);
    }
}
