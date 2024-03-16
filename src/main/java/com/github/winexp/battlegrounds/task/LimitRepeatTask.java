package com.github.winexp.battlegrounds.task;

import org.jetbrains.annotations.Range;

public class LimitRepeatTask extends ScheduledTask {
    public static final LimitRepeatTask NONE_TASK = new LimitRepeatTask(AbstractTask.NONE_RUNNABLE, AbstractTask.NONE_RUNNABLE, -1, 1, -1);
    private final Runnable fixedRunnable;
    private final long unitTicks;
    private int count;

    public LimitRepeatTask(Runnable trigger, Runnable triggerEnd, long delay, @Range(from = 1, to = Long.MAX_VALUE) long unitTicks, int count) {
        super(trigger, delay);
        fixedRunnable = () -> {
            if (this.count < 0) {
                throw new TaskCancelledException(true);
            }

            try {
                if (this.count == 0) {
                    super.preTriggerRunnable = () -> {
                        triggerEnd.run();
                        throw new TaskCancelledException(false);
                    };
                }
                super.getRunnable().run();
            } catch (TaskCancelledException e) {
                e.ensureNotAbsolute();
                this.count--;
                this.delay = this.getUnitTicks();
            }

            if (this.count < 0) {
                throw new TaskCancelledException(false);
            }
        };

        this.count = count;
        this.unitTicks = unitTicks;
    }

    @Override
    public Runnable getRunnable() {
        return this.fixedRunnable;
    }

    public int getCount() {
        return this.count;
    }

    public long getUnitTicks() {
        return this.unitTicks;
    }
}
