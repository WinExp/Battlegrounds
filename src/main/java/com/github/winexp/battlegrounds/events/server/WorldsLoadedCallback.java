package com.github.winexp.battlegrounds.events.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

public interface WorldsLoadedCallback {
    Event<WorldsLoadedCallback> EVENT = EventFactory.createArrayBacked(WorldsLoadedCallback.class,
            (listeners) -> (server) -> {
                for (WorldsLoadedCallback listener : listeners){
                    ActionResult actionResult = listener.interact(server);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(MinecraftServer server);
}
