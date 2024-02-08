package com.github.winexp.battlegrounds.events.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

public interface ServerSavingCallback {
    Event<ServerSavingCallback> EVENT = EventFactory.createArrayBacked(ServerSavingCallback.class,
            (listeners) -> (server, suppressLogs) -> {
                for (ServerSavingCallback listener : listeners) {
                    ActionResult actionResult = listener.interact(server, suppressLogs);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }
                return ActionResult.PASS;
            });

    ActionResult interact(MinecraftServer server, boolean suppressLogs);
}
