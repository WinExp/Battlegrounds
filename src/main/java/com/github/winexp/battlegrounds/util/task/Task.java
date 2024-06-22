package com.github.winexp.battlegrounds.util.task;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.CancellationException;

public abstract class Task {
    private boolean cancelled = false;

    protected Task() {
    }

    public ExecuteStage getExecuteStage() {
        return ExecuteStage.END;
    }

    public boolean isExecutable() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public abstract void tick() throws CancellationException;

    @Contract(value = "-> this", pure = true)
    public Task schedule() {
        TaskScheduler.INSTANCE.schedule(this);
        return this;
    }

    public boolean isScheduled() {
        return this.isExecutable() && TaskScheduler.INSTANCE.isScheduled(this);
    }

    public enum ExecuteStage {
        BEGIN, END
    }
}
