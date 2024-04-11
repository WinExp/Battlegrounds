package com.github.winexp.battlegrounds.task;

import com.github.winexp.battlegrounds.util.time.Duration;

import java.util.concurrent.CancellationException;

public abstract class ScheduledTask extends AbstractTask {
    public static final ScheduledTask NONE_TASK = new ScheduledTask(Duration.INFINITY) {
        @Override
        public void run() throws CancellationException {
        }
    };
    protected int delay;

    public ScheduledTask(Duration delay) {
        super();
        this.delay = delay.toTicks();
        if (delay.toTicks() < 0) {
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

    public int getDelayTicks() {
        if (this.isCancelled()) return -1;
        else return this.delay;
    }
}