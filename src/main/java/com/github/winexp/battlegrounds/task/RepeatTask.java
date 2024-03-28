package com.github.winexp.battlegrounds.task;

import java.util.concurrent.CancellationException;
import java.util.function.IntSupplier;

public abstract class RepeatTask extends ScheduledTask {
    public static final RepeatTask NONE_TASK = new RepeatTask(-1, -1) {
        @Override
        public void onTriggered() throws CancellationException {
        }
    };
    private final IntSupplier repeatDelay;

    public RepeatTask(int delay, IntSupplier repeatDelay) {
        super(delay);
        this.repeatDelay = repeatDelay;
    }

    public RepeatTask(int delay, int repeatDelay) {
        this(delay, () -> repeatDelay);
    }

    public long getRepeatDelay() {
        return this.repeatDelay.getAsInt();
    }

    public void resetDelay() {
        this.delay = this.repeatDelay.getAsInt();
    }

    public abstract void onTriggered() throws CancellationException;

    @Override
    public void run() throws CancellationException {
        this.onTriggered();
        this.resetDelay();
    }
}