package com.github.winexp.battlegrounds.helper.task;

import com.github.winexp.battlegrounds.events.server.ServerTickCallback;
import com.github.winexp.battlegrounds.events.task.TaskTriggerCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

    public TaskScheduler(){
        ServerTickCallback.EVENT.register(this::tick);
    }

    private ActionResult tick(MinecraftServer server){
        for (Task task : tasks){
            if (task.isCancelled()) tasks.remove(task);
            try{
                if (task instanceof TaskLater later){
                    later.decreaseDelay();
                    if (later.getDelay() <= 0){
                        TaskTriggerCallback.EVENT.invoker().interact(task);
                        later.run();
                        tasks.remove(task);
                    }
                }
                else if (task instanceof TaskTimer timer){
                    if (timer.getDelay() < 0){
                        tasks.remove(task);
                        continue;
                    }
                    timer.decreaseDelay();
                    if (timer.getDelay() <= 0){
                        TaskTriggerCallback.EVENT.invoker().interact(task);
                        timer.run();
                        timer.resetDelay();
                    }
                }
            }
            catch (RunnableCancelledException e){
                tasks.remove(task);
            }
        }

        return ActionResult.PASS;
    }

    public void runTask(Task task){
        tasks.add(task);
    }
}
