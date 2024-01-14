package com.github.winexp.battlegrounds.helper.task;

public class TaskLater extends Task{
    public final static TaskLater NONE_TASK = new TaskLater(Task.NONE_RUNNABLE, 0);
    private long delay;

    public long getDelay() { return delay; }
    public void decreaseDelay() { this.delay--; }

    public TaskLater(Runnable runnable, long delay){
        super(runnable);
        this.delay = delay;
    }
}