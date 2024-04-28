package com.github.winexp.battlegrounds.mixin.structure;

import com.github.winexp.battlegrounds.structure.StructureKeys;
import com.github.winexp.battlegrounds.structure.StructurePoolKeys;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Structures.class)
public abstract class structures_StructuresMixin {
    @Unique
    private static void registerDefault(Registerable<Structure> structureRegisterable,
                                        RegistryEntryLookup<Biome> biomeRegistryEntryLookup,
                                        RegistryEntryLookup<StructurePool> structurePoolRegistryEntryLookup,
                                        RegistryKey<Structure> structureKey,
                                        RegistryKey<StructurePool> structurePoolKey, int yOffset) {
        structureRegisterable.register(structureKey, new JigsawStructure(Structures.createConfig(
                biomeRegistryEntryLookup.getOrThrow(BiomeTags.PILLAGER_OUTPOST_HAS_STRUCTURE),
                Map.of(),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                StructureTerrainAdaptation.NONE
        ), structurePoolRegistryEntryLookup.getOrThrow(structurePoolKey), 1,
                ConstantHeightProvider.create(YOffset.fixed(yOffset)), false, Heightmap.Type.WORLD_SURFACE_WG));
    }

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void onBootstrap(Registerable<Structure> structureRegisterable, CallbackInfo ci,
                                    @Local(name = "registryEntryLookup") RegistryEntryLookup<Biome> biomeRegistryEntryLookup,
                                    @Local(name = "registryEntryLookup2") RegistryEntryLookup<StructurePool> structurePoolRegistryEntryLookup) {
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.ANCIENT_RUINS, StructurePoolKeys.ANCIENT_RUINS, 1);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.CANOPIES, StructurePoolKeys.CANOPIES, 1);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.DESERT_HUT, StructurePoolKeys.DESERT_HUT, 0);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.IZAKAYA, StructurePoolKeys.IZAKAYA, 0);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.KIOSK, StructurePoolKeys.KIOSK, -2);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.LIBRARY, StructurePoolKeys.LIBRARY, 1);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.MEDIEVAL_FORTRESS, StructurePoolKeys.MEDIEVAL_FORTRESS, 0);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.MEDIEVAL_LIBRARY, StructurePoolKeys.MEDIEVAL_LIBRARY, 1);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.RELIC_OF_FANTASY, StructurePoolKeys.RELIC_OF_FANTASY, 0);
        registerDefault(structureRegisterable, biomeRegistryEntryLookup, structurePoolRegistryEntryLookup, StructureKeys.WINE_SHOP, StructurePoolKeys.WINE_SHOP, 1);
    }
}
