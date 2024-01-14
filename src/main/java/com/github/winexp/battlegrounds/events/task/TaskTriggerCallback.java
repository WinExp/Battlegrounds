package com.github.winexp.battlegrounds.events.task;

import com.github.winexp.battlegrounds.helper.task.Task;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface TaskTriggerCallback {
    Event<TaskTriggerCallback> EVENT = EventFactory.createArrayBacked(TaskTriggerCallback.class,
            (listeners) -> (task) -> {
                for (TaskTriggerCallback listener : listeners){
                    ActionResult actionResult = listener.interact(task);
                    if (actionResult != ActionResult.PASS) return actionResult;
                }

                return ActionResult.PASS;
            });

    ActionResult interact(Task task);
}
