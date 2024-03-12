package com.github.winexp.battlegrounds.task;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CopyOnWriteArrayList;

public class TaskExecutor {
    public static final TaskExecutor INSTANCE = new TaskExecutor();

    private final CopyOnWriteArrayList<AbstractTask> tasks = new CopyOnWriteArrayList<>();

    public TaskExecutor() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (AbstractTask task : tasks) {
            if (task.isCancelled()) tasks.remove(task);
            try {
                task.run();
            } catch (TaskCancelledException e) {
                tasks.remove(task);
            }
        }
    }

    public void execute(AbstractTask task) {
        if (tasks.contains(task)) return;
        tasks.add(task);
    }

    public void stopAllTask() {
        tasks.clear();
    }
}
