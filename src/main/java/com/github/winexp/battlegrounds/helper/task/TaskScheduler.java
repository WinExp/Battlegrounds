package com.github.winexp.battlegrounds.helper.task;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("SameReturnValue")
public class TaskScheduler {
    public final static TaskScheduler INSTANCE = new TaskScheduler();

    private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();

    public TaskScheduler() {
        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
    }

    private void onTick(MinecraftServer server) {
        for (Task task : tasks) {
            if (task.isCancelled()) tasks.remove(task);
            try {
                task.run();
            } catch (RunnableCancelledException e) {
                tasks.remove(task);
            }
        }
    }

    public void runTask(Task task) {
        if (tasks.contains(task)) return;
        tasks.add(task);
    }

    public void stopAllTask() {
        tasks.clear();
    }
}
