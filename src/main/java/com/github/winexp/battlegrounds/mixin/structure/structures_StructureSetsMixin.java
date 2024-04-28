package com.github.winexp.battlegrounds.mixin.structure;

import com.github.winexp.battlegrounds.structure.StructureKeys;
import com.github.winexp.battlegrounds.structure.StructureSetKeys;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSets;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureSets.class)
public interface structures_StructureSetsMixin {
    @Unique
    private static int getSalt(RegistryKey<?> structureSetKey) {
        return Math.abs(structureSetKey.getValue().hashCode());
    }

    @Unique
    private static void registerDefault(Registerable<StructureSet> structureSetRegisterable,
                                        RegistryEntryLookup<Structure> structureRegistryEntryLookup,
                                        RegistryKey<StructureSet> structureSetKey,
                                        RegistryKey<Structure> structureKey,
                                        int spacing, int separation) {
        structureSetRegisterable.register(structureSetKey, new StructureSet(structureRegistryEntryLookup.getOrThrow(structureKey),
                new RandomSpreadStructurePlacement(spacing, separation, SpreadType.LINEAR, getSalt(structureSetKey))));
    }

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void onBootstrap(Registerable<StructureSet> structureSetRegisterable, CallbackInfo ci,
                                    @Local(name = "registryEntryLookup") RegistryEntryLookup<Structure> structureRegistryEntryLookup) {
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.ANCIENT_RUINS, StructureKeys.ANCIENT_RUINS, 15, 4);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.CANOPIES, StructureKeys.CANOPIES, 26, 7);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.DESERT_HUT, StructureKeys.DESERT_HUT, 28, 7);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.IZAKAYA, StructureKeys.IZAKAYA, 32, 10);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.KIOSK, StructureKeys.KIOSK, 30, 8);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.LIBRARY, StructureKeys.LIBRARY, 25, 6);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.MEDIEVAL_FORTRESS, StructureKeys.MEDIEVAL_FORTRESS, 26, 8);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.MEDIEVAL_LIBRARY, StructureKeys.MEDIEVAL_LIBRARY, 34, 12);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.RELIC_OF_FANTASY, StructureKeys.RELIC_OF_FANTASY, 23, 5);
        registerDefault(structureSetRegisterable, structureRegistryEntryLookup, StructureSetKeys.WINE_SHOP, StructureKeys.WINE_SHOP, 27, 8);
    }
}
