package com.github.winexp.battlegrounds.util.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    public static final TaskScheduler INSTANCE = new TaskScheduler();

    private final List<AbstractTask> tasks = new CopyOnWriteArrayList<>();

    public TaskScheduler() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> this.startTick());
            ClientTickEvents.END_CLIENT_TICK.register(client -> this.endTick());
        } else {
            ServerTickEvents.START_SERVER_TICK.register(server -> this.startTick());
            ServerTickEvents.END_SERVER_TICK.register(server -> this.endTick());
        }
    }

    private void startTick() {
        for (AbstractTask task : this.tasks) {
            if (task.getExecuteStage() != AbstractTask.ExecuteStage.BEGIN) continue;
            this.tryExecute(task);
        }
    }

    private void endTick() {
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
