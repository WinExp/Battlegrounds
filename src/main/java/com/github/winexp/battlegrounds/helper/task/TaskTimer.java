package com.github.winexp.battlegrounds.helper.task;

import java.util.function.LongSupplier;

public class TaskTimer extends TaskLater {
    public final static TaskTimer NONE_TASK = new TaskTimer(Task.NONE_RUNNABLE, -1, -1);
    private final Runnable fixedRunnable;
    private final LongSupplier ticks;

    public TaskTimer(Runnable runnable, long delay, LongSupplier ticks) {
        super(runnable, delay);

        fixedRunnable = () -> {
            try {
                super.run();
            } catch (RunnableCancelledException e) {
                if (e.getEnforceCancel()) {
                    throw e;
                }
            }

            if (this.delay <= 0) {
                this.delay = ticks.getAsLong();
            }
        };

        this.ticks = ticks;
    }

    public TaskTimer(Runnable runnable, long delay, long ticks) {
        this(runnable, delay, () -> ticks);
    }

    @Override
    public void run() {
        fixedRunnable.run();
    }

    @Override
    public Runnable getRunnable() {
        return fixedRunnable;
    }

    public long getTicks() {
        return ticks.getAsLong();
    }
}