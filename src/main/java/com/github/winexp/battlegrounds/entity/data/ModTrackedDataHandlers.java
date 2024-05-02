package com.github.winexp.battlegrounds.entity.data;

import com.github.winexp.battlegrounds.entity.projectile.thrown.AbstractThrownPropEntity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class ModTrackedDataHandlers {
    public static final TrackedDataHandler<AbstractThrownPropEntity.FuseMode> PROP_THROWN_FUSE_MODE = TrackedDataHandler.ofEnum(AbstractThrownPropEntity.FuseMode.class);

    static {
        TrackedDataHandlerRegistry.register(PROP_THROWN_FUSE_MODE);
    }
}
