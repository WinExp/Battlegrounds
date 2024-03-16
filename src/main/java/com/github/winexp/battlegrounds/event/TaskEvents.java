package com.github.winexp.battlegrounds.event;

import com.github.winexp.battlegrounds.task.AbstractTask;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class TaskEvents {
    public static Event<Triggered> TRIGGERED = EventFactory.createArrayBacked(Triggered.class,
            (listeners) -> (task) -> {
                for (Triggered listener : listeners) {
                    listener.triggered(task);
                }
            });

    @FunctionalInterface
    public interface Triggered {
        void triggered(AbstractTask task);
    }
}
