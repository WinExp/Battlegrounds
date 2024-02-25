package com.github.winexp.battlegrounds.entity;

import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class EntityTypes<T extends Entity> extends net.minecraft.entity.EntityType<T> {
    public final static net.minecraft.entity.EntityType<ChannelingArrowEntity> CHANNELING_ARROW =
            FabricEntityTypeBuilder.<ChannelingArrowEntity>create(SpawnGroup.MISC, ChannelingArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build();
    public final static net.minecraft.entity.EntityType<FlashBangEntity> FLASH_BANG =
            FabricEntityTypeBuilder.<FlashBangEntity>create(SpawnGroup.MISC, FlashBangEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build();

    private EntityTypes(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, int maxTrackDistance, int trackTickInterval, FeatureSet requiredFeatures) {
        super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, maxTrackDistance, trackTickInterval, requiredFeatures);
    }

    public static void registerEntities() {
        Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier("battlegrounds", "channeling_arrow"),
                CHANNELING_ARROW);
        Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier("battlegrounds", "flash_bang"),
                FLASH_BANG
        );
    }
}
