package com.github.winexp.battlegrounds.entity;

import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import com.github.winexp.battlegrounds.entity.projectile.thrown.FlashBangEntity;
import com.github.winexp.battlegrounds.entity.projectile.thrown.MolotovEntity;
import com.google.common.collect.ImmutableSet;
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
            EntityType.Builder.<ChannelingArrowEntity>create(ChannelingArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5F, 0.5F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(1)
                    .build()
    );
    public static final net.minecraft.entity.EntityType<FlashBangEntity> FLASH_BANG = register("flash_bang",
            EntityType.Builder.<FlashBangEntity>create(FlashBangEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(1)
                    .build()
    );
    public static final net.minecraft.entity.EntityType<MolotovEntity> MOLOTOV = register("molotov",
            EntityType.Builder.<MolotovEntity>create(MolotovEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(1)
                    .build()
    );

    private EntityTypes(EntityFactory<T> factory, SpawnGroup spawnGroup, boolean saveable, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> canSpawnInside, EntityDimensions dimensions, float spawnBoxScale, int maxTrackDistance, int trackTickInterval, FeatureSet requiredFeatures) {
        super(factory, spawnGroup, saveable, summonable, fireImmune, spawnableFarFromPlayer, canSpawnInside, dimensions, spawnBoxScale, maxTrackDistance, trackTickInterval, requiredFeatures);
    }

    public static <T extends Entity> EntityType<T> register(String name, EntityType<T> entityType) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier("battlegrounds", name),
                entityType
        );
    }

    public static void bootstrap() {
    }
}
