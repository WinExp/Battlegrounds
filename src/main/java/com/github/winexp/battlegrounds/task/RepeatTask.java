package com.github.winexp.battlegrounds.task;

import java.util.function.LongSupplier;

public class RepeatTask extends ScheduledTask {
    public static final RepeatTask NONE_TASK = new RepeatTask(AbstractTask.NONE_RUNNABLE, -1, -1);
    private final Runnable fixedRunnable;
    private final LongSupplier ticks;

    public RepeatTask(Runnable runnable, long delay, LongSupplier ticks) {
        super(runnable, delay);

        fixedRunnable = () -> {
            try {
                super.run();
            } catch (TaskCancelledException e) {
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

    public RepeatTask(Runnable runnable, long delay, long ticks) {
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