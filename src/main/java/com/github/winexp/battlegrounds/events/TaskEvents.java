package com.github.winexp.battlegrounds.events;

import com.github.winexp.battlegrounds.task.Task;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface TaskEvents {
    Event<TaskEvents> TRIGGERED = EventFactory.createArrayBacked(TaskEvents.class,
            (listeners) -> (task) -> {
                for (TaskEvents listener : listeners) {
                    ActionResult actionResult = listener.interact(task);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(Task task);
}
