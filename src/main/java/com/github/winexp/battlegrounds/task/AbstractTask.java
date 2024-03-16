package com.github.winexp.battlegrounds.task;

import com.github.winexp.battlegrounds.event.TaskEvents;

public abstract class AbstractTask {
    public static final Runnable NONE_RUNNABLE = () -> {
    };
    private final Runnable runnable;
    private boolean cancelled = false;

    protected AbstractTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public void run() {
        this.getRunnable().run();
        TaskEvents.TRIGGERED.invoker().triggered(this);
    }
}
