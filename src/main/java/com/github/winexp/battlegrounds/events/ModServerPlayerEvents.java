package com.github.winexp.battlegrounds.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ModServerPlayerEvents {
    Event<ModServerPlayerEvents> ALLOW_NATURAL_REGEN = EventFactory.createArrayBacked(ModServerPlayerEvents.class,
            (listeners) -> (instance) -> {
                for (ModServerPlayerEvents listener : listeners) {
                    if (!listener.interact(instance)) return false;
                }
                return true;
            });

    boolean interact(PlayerEntity instance);
}
