package com.github.winexp.battlegrounds.world.gen.structure;

import com.github.winexp.battlegrounds.world.gen.structure.processor.StructureProcessorLists;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

public class StructurePools extends net.minecraft.structure.pool.StructurePools {
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

    public static RegistryKey<StructurePool> of(String id) {
        return RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier("battlegrounds", id));
    }

    public static void register(Registerable<StructurePool> structurePoolsRegisterable) {
        RegistryEntry<StructurePool> emptyEntry = structurePoolsRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL)
                .getOrThrow(StructurePools.EMPTY);
        RegistryEntryLookup<StructureProcessorList> structureProcessorListsRegistryEntryLookup = structurePoolsRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
        structurePoolsRegisterable.register(StructurePools.ANCIENT_RUINS, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:ancient_ruin_land"), 1),
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:ancient_ruin_ocean"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.CANOPIES, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:canopies"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.DESERT_HUT, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:desert_hut"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.IZAKAYA, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:izakaya"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.KIOSK, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:kiosk"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.LIBRARY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:library"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.MEDIEVAL_FORTRESS, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofProcessedSingle("battlegrounds:medieval_fortress", structureProcessorListsRegistryEntryLookup.getOrThrow(StructureProcessorLists.MEDIEVAL_FORTRESS)), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.MEDIEVAL_LIBRARY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofProcessedSingle("battlegrounds:medieval_library", structureProcessorListsRegistryEntryLookup.getOrThrow(StructureProcessorLists.MEDIEVAL_LIBRARY)), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.RELIC_OF_FANTASY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofProcessedSingle("battlegrounds:relic_of_fantasy", structureProcessorListsRegistryEntryLookup.getOrThrow(StructureProcessorLists.RELIC_OF_FANTASY)), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePools.WINE_SHOP, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:wine_shop"), 1)
        ), StructurePool.Projection.RIGID));
    }
}
