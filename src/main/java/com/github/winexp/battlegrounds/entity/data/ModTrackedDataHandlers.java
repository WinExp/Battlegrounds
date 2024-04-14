package com.github.winexp.battlegrounds.entity.data;

import com.github.winexp.battlegrounds.entity.projectile.FlashBangEntity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;

public class ModTrackedDataHandlers {
    public static final TrackedDataHandler<FlashBangEntity.FuseMode> FLASH_BANG_FUSE_MODE = TrackedDataHandler.ofEnum(FlashBangEntity.FuseMode.class);

    static {
        TrackedDataHandlerRegistry.register(FLASH_BANG_FUSE_MODE);
    }
}
