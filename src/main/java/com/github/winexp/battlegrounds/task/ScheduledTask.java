package com.github.winexp.battlegrounds.task;

import java.util.concurrent.CancellationException;

public abstract class ScheduledTask extends AbstractTask {
    public static final ScheduledTask NONE_TASK = new ScheduledTask(-1) {
        @Override
        public void run() throws CancellationException {
        }
    };
    protected int delay;

    public ScheduledTask(int delay) {
        super();
        this.delay = delay;
    }

    @Override
    public void tick() throws CancellationException {
        if (delay < 0) {
            this.cancel();
            throw new CancellationException();
        }
        this.delay--;
        if (this.delay <= 0) {
            this.run();
        }
    }

    public int getDelay() {
        return this.delay;
    }
}