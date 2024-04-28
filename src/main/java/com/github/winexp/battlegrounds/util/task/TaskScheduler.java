package com.github.winexp.battlegrounds.util.task;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    public static final TaskScheduler INSTANCE = new TaskScheduler();

    private final List<AbstractTask> tasks = new CopyOnWriteArrayList<>();

    public TaskScheduler() {
        ServerTickEvents.START_SERVER_TICK.register(this::startTick);
        ServerTickEvents.END_SERVER_TICK.register(this::endTick);
    }

    private void startTick(MinecraftServer server) {
        for (AbstractTask task : this.tasks) {
            if (task.getExecuteStage() != AbstractTask.ExecuteStage.BEGIN) continue;
            this.tryExecute(task);
        }
    }

    private void endTick(MinecraftServer server) {
        for (AbstractTask task : this.tasks) {
            if (task.getExecuteStage() != AbstractTask.ExecuteStage.END) continue;
            this.tryExecute(task);
        }
    }

    private void tryExecute(AbstractTask task) {
        if (task.isCancelled()) this.tasks.remove(task);
        try {
            task.tick();
        } catch (CancellationException e) {
            task.cancel();
        }
    }

    public boolean isRunning(AbstractTask task) {
        return this.tasks.contains(task) && !task.isCancelled();
    }

    public void schedule(AbstractTask task) {
        if (this.tasks.contains(task)) return;
        this.tasks.add(task);
    }

    public void cancelAllTasks() {
        for (AbstractTask task : this.tasks) {
            task.cancel();
        }
    }
}
