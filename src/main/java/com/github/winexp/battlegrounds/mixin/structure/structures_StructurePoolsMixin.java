package com.github.winexp.battlegrounds.mixin.structure;

import com.github.winexp.battlegrounds.structure.StructurePoolKeys;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: 需要优化结构
@Mixin(StructurePools.class)
public abstract class structures_StructurePoolsMixin {

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void onBootstrap(Registerable<StructurePool> structurePoolsRegisterable, CallbackInfo ci,
                                    @Local(name = "registryEntry") RegistryEntry<StructurePool> emptyEntry) {
        structurePoolsRegisterable.register(StructurePoolKeys.ANCIENT_RUINS, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:ancient_ruin_land"), 1),
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:ancient_ruin_ocean"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.CANOPIES, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:canopies"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.DESERT_HUT, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:desert_hut"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.IZAKAYA, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:izakaya"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.KIOSK, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:kiosk"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.LIBRARY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:library"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.MEDIEVAL_FORTRESS, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:medieval_fortress"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.MEDIEVAL_LIBRARY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:medieval_library"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.RELIC_OF_FANTASY, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:relic_of_fantasy"), 1)
        ), StructurePool.Projection.RIGID));
        structurePoolsRegisterable.register(StructurePoolKeys.WINE_SHOP, new StructurePool(emptyEntry, ImmutableList.of(
                Pair.of(StructurePoolElement.ofSingle("battlegrounds:wine_shop"), 1)
        ), StructurePool.Projection.RIGID));
    }
}
