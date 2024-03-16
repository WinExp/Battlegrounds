package com.github.winexp.battlegrounds.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public class ModServerPlayerEvents {
    public static Event<AllowNaturalRegen> ALLOW_NATURAL_REGEN = EventFactory.createArrayBacked(AllowNaturalRegen.class,
            (listeners) -> (instance) -> {
                for (AllowNaturalRegen listener : listeners) {
                    if (!listener.isAllow(instance)) return false;
                }
                return true;
            });

    @FunctionalInterface
    public interface AllowNaturalRegen {
        boolean isAllow(PlayerEntity instance);
    }
}
