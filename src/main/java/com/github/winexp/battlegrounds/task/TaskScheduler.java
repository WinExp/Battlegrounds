package com.github.winexp.battlegrounds.task;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    public static final TaskScheduler INSTANCE = new TaskScheduler();

    private final CopyOnWriteArrayList<AbstractTask> tasks = new CopyOnWriteArrayList<>();

    public TaskScheduler() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer server) {
        for (AbstractTask task : this.tasks) {
            if (task.isCancelled()) this.tasks.remove(task);
            try {
                task.tick();
            } catch (CancellationException e) {
                task.cancel();
            }
        }
    }

    public void schedule(AbstractTask task) {
        if (this.tasks.contains(task)) return;
        this.tasks.add(task);
    }

    public void cancelAllTask() {
        for (AbstractTask task : this.tasks) {
            task.cancel();
        }
    }
}
