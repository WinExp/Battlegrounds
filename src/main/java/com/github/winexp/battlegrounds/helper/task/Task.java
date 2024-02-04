package com.github.winexp.battlegrounds.helper.task;

import com.github.winexp.battlegrounds.events.task.TaskTriggerCallback;

public abstract class Task {
    private boolean cancelled = false;
    private final Runnable runnable;
    public final static Runnable NONE_RUNNABLE = () -> {};

    public void cancel() { cancelled = true; }
    public boolean isCancelled() { return cancelled; }
    public Runnable getRunnable() { return runnable; }

    public void run(){
        runnable.run();
        TaskTriggerCallback.EVENT.invoker().interact(this);
    }

    protected Task(Runnable runnable){
        this.runnable = runnable;
    }
}
