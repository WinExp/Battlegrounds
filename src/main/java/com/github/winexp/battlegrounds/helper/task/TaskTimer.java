package com.github.winexp.battlegrounds.helper.task;

import java.util.function.LongSupplier;

public class TaskTimer extends Task{
    public final static TaskTimer NONE_TASK = new TaskTimer(Task.NONE_RUNNABLE, -1, -1);
    private final LongSupplier ticks;
    private long delay;

    public long getDelay() { return delay; }
    public void resetDelay() { delay = ticks.getAsLong(); }
    public long getTicks() { return ticks.getAsLong(); }
    public void decreaseDelay() { this.delay--; }

    public TaskTimer(Runnable runnable, long delay, long ticks){
        super(runnable);
        this.delay = delay;
        this.ticks = () -> ticks;
    }

    public TaskTimer(Runnable runnable, long delay, LongSupplier ticks){
        super(runnable);
        this.delay = delay;
        this.ticks = ticks;
    }
}