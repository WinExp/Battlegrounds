package com.github.winexp.battlegrounds.util.task;

import java.util.concurrent.CancellationException;

public abstract class RepeatTask extends ScheduledTask {
    public static final RepeatTask NONE_TASK = new RepeatTask(-1, -1) {
        @Override
        public void onTriggered() throws CancellationException {
        }
    };
    private final int repeatDelayTicks;

    public RepeatTask(int delayTicks, int repeatDelayTicks) {
        super(delayTicks);
        this.repeatDelayTicks = repeatDelayTicks;
    }

    public int getRepeatDelayTicks() {
        return this.repeatDelayTicks;
    }

    protected void resetDelay() {
        this.setDelayTicks(this.repeatDelayTicks);
    }

    public abstract void onTriggered() throws CancellationException;

    @Override
    public void run() throws CancellationException {
        this.onTriggered();
        this.resetDelay();
    }
}