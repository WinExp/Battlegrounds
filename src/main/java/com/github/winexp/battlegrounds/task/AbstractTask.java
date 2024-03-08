package com.github.winexp.battlegrounds.task;

import com.github.winexp.battlegrounds.events.TaskEvents;

public abstract class AbstractTask {
    public static final Runnable NONE_RUNNABLE = () -> {
    };
    private final Runnable runnable;
    private boolean cancelled = false;

    protected AbstractTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void run() {
        runnable.run();
        TaskEvents.TRIGGERED.invoker().triggered(this);
    }
}
