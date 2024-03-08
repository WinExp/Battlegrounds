package com.github.winexp.battlegrounds.events;

import com.github.winexp.battlegrounds.task.AbstractTask;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface TaskEvents {
    Event<Triggered> TRIGGERED = EventFactory.createArrayBacked(Triggered.class,
            (listeners) -> (task) -> {
                for (Triggered listener : listeners) {
                    listener.triggered(task);
                }
            });

    @FunctionalInterface
    interface Triggered {
        void triggered(AbstractTask task);
    }
}
