package com.github.winexp.battlegrounds.helper.task;

public class TaskTimer extends Task{
    public final static TaskTimer NONE_TASK = new TaskTimer(Task.NONE_RUNNABLE, -1, 0);
    private final long ticks;
    private long delay;

    public long getDelay() { return delay; }
    public void resetDelay() { delay = ticks; }
    public long getTicks() { return ticks; }
    public void decreaseDelay() { this.delay--; }

    public TaskTimer(Runnable runnable, long delay, long ticks){
        super(runnable);
        this.delay = delay;
        this.ticks = ticks;
    }
}