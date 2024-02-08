package com.github.winexp.battlegrounds.entity;

import com.github.winexp.battlegrounds.entity.projectile.ChannelingArrowEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityTypes {
    public final static EntityType<ChannelingArrowEntity> CHANNELING_ARROW =
            FabricEntityTypeBuilder.<ChannelingArrowEntity>create(SpawnGroup.MISC, ChannelingArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build();

    public static void registerEntities() {
        Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier("battlegrounds", "channeling_arrow"),
                CHANNELING_ARROW);
    }
}
