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
        for (AbstractTask task : this.tasks) {
            if (task.isCancelled()) this.tasks.remove(task);
            try {
                task.run();
            } catch (TaskCancelledException e) {
                this.tasks.remove(task);
            }
        }
    }

    public void execute(AbstractTask task) {
        if (this.tasks.contains(task)) return;
        this.tasks.add(task);
    }

    public void cancelAllTask() {
        for (AbstractTask task : this.tasks) {
            task.cancel();
            this.tasks.remove(task);
        }
    }
}
