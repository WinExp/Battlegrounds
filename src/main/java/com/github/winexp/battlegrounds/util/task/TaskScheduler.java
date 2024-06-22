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

    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    private TaskScheduler() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientTickEvents.START_CLIENT_TICK.register(client -> this.startTick());
            ClientTickEvents.END_CLIENT_TICK.register(client -> this.endTick());
        } else {
            ServerTickEvents.START_SERVER_TICK.register(server -> this.startTick());
            ServerTickEvents.END_SERVER_TICK.register(server -> this.endTick());
        }
    }

    private void startTick() {
        for (Task task : this.tasks) {
            if (task.getExecuteStage() != Task.ExecuteStage.BEGIN) continue;
            if (!this.tryExecute(task)) this.tasks.remove(task);
        }
    }

    private void endTick() {
        for (Task task : this.tasks) {
            if (task.getExecuteStage() != Task.ExecuteStage.END) continue;
            if (!this.tryExecute(task)) this.tasks.remove(task);
        }
    }

    private boolean tryExecute(Task task) {
        if (task.isExecutable()) return false;
        try {
            task.tick();
            return true;
        } catch (CancellationException e) {
            task.cancel();
            return false;
        }
    }

    public boolean isScheduled(Task task) {
        return this.tasks.contains(task) && !task.isExecutable();
    }

    public void schedule(Task task) {
        if (this.tasks.contains(task)) return;
        this.tasks.add(task);
    }

    public void cancelAllTasks() {
        for (Task task : this.tasks) {
            task.cancel();
        }
    }
}
