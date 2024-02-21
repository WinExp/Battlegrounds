package com.github.winexp.battlegrounds.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface PlayerRespawnCallback {
    Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class,
            (listeners) -> (instance) -> {
                for (PlayerRespawnCallback listener : listeners) {
                    ActionResult actionResult = listener.interact(instance);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(ServerPlayerEntity instance);
}
