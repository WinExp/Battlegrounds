package com.github.winexp.battlegrounds.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface ModServerPlayerEvents {
    Event<AllowNaturalRegen> ALLOW_NATURAL_REGEN = EventFactory.createArrayBacked(AllowNaturalRegen.class,
            (listeners) -> (instance) -> {
                for (AllowNaturalRegen listener : listeners) {
                    if (!listener.allow(instance)) return false;
                }
                return true;
            });

    @FunctionalInterface
    interface AllowNaturalRegen {
        boolean allow(PlayerEntity instance);
    }
}
