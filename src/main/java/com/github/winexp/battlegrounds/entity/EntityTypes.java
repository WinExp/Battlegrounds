package com.github.winexp.battlegrounds.entity;

import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.entity.projectile.thrown.MolotovEntity;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

public class EntityTypes<T extends Entity> extends net.minecraft.entity.EntityType<T> {
    public static final net.minecraft.entity.EntityType<ChannelingArrowEntity> CHANNELING_ARROW = register("channeling_arrow",
            FabricEntityTypeBuilder.<ChannelingArrowEntity>create(SpawnGroup.MISC, ChannelingArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build()
    );
    public static final net.minecraft.entity.EntityType<FlashBangEntity> FLASH_BANG = register("flash_bang",
            FabricEntityTypeBuilder.<FlashBangEntity>create(SpawnGroup.MISC, FlashBangEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build()
    );
    public static final net.minecraft.entity.EntityType<MolotovEntity> MOLOTOV = register("molotov",
            FabricEntityTypeBuilder.<MolotovEntity>create(SpawnGroup.MISC, MolotovEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.25F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build()
    );

    private EntityTypes(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, int maxTrackDistance, int trackTickInterval, FeatureSet requiredFeatures) {
        super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, maxTrackDistance, trackTickInterval, requiredFeatures);
    }

    public static <T extends Entity> EntityType<T> register(String name, EntityType<T> entityType) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier("battlegrounds", name),
                entityType
        );
    }

    public static void registerEntityTypes() {
    }
}
