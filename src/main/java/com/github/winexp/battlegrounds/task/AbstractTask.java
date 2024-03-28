package com.github.winexp.battlegrounds.task;

import java.util.concurrent.CancellationException;

public abstract class AbstractTask {
    private boolean cancelled = false;

    protected AbstractTask() {
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public abstract void tick() throws CancellationException;

    public abstract void run() throws CancellationException;
}
