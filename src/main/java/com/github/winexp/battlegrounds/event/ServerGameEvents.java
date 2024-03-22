package com.github.winexp.battlegrounds.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;

public class ServerGameEvents {
    public static Event<StageChanged> STAGE_CHANGED = EventFactory.createArrayBacked(StageChanged.class,
            (listeners) -> (id) -> {
                for (StageChanged listener : listeners) {
                    listener.onStageChanged(id);
                }
            });

    @FunctionalInterface
    public interface StageChanged {
        void onStageChanged(Identifier id);
    }
}
