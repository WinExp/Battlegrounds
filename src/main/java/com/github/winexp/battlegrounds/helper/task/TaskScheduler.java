package com.github.winexp.battlegrounds.helper.task;

import com.github.winexp.battlegrounds.events.server.ServerTickCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;

import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    public final static TaskScheduler INSTANCE = new TaskScheduler();

    private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

    private TaskScheduler(){
        ServerTickCallback.EVENT.register(this::tick);
    }

    private ActionResult tick(MinecraftServer server){
        for (Task task : tasks){
            if (task.isCancelled()) tasks.remove(task);
            try{
                task.run();
            }
            catch (RunnableCancelledException e){
                tasks.remove(task);
            }
        }

        return ActionResult.PASS;
    }

    public void runTask(Task task){
        if (tasks.contains(task)) return;
        tasks.add(task);
    }
    public void stopAllTask(){
        tasks.clear();
    }
}
