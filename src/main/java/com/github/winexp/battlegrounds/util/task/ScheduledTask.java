package com.github.winexp.battlegrounds.util.task;

import java.util.concurrent.CancellationException;

public abstract class ScheduledTask extends Task {
    public static final ScheduledTask NONE_TASK = new ScheduledTask(-1) {
        @Override
        public void run() throws CancellationException {
        }
    };
    private int delay;

    public ScheduledTask(int delayTicks) {
        super();
        this.delay = delayTicks;
        if (this.delay < 0) {
            this.cancel();
        }
    }

    @Override
    public void tick() throws CancellationException {
        if (this.delay < 0) {
            this.cancel();
            throw new CancellationException();
        }
        this.delay--;
        if (this.delay <= 0) {
            this.delay = -1;
            this.run();
        }
    }

    @Override
    public ScheduledTask schedule() {
        super.schedule();
        return this;
    }

    public abstract void run() throws CancellationException;

    public int getDelayTicks() {
        if (this.isScheduled()) return -1;
        else return this.delay;
    }

    protected void setDelayTicks(int delayTicks) {
        this.delay = delayTicks;
    }
}