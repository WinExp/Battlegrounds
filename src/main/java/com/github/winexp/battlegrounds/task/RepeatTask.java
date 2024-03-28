package com.github.winexp.battlegrounds.task;

import com.github.winexp.battlegrounds.util.time.Duration;

import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

public abstract class RepeatTask extends ScheduledTask {
    public static final RepeatTask NONE_TASK = new RepeatTask(Duration.INFINITY, Duration.INFINITY) {
        @Override
        public void onTriggered() throws CancellationException {
        }
    };
    private final Supplier<Duration> repeatDelay;

    public RepeatTask(Duration delay, Supplier<Duration> repeatDelay) {
        super(delay);
        this.repeatDelay = repeatDelay;
    }

    public RepeatTask(Duration delay, Duration repeatDelay) {
        this(delay, () -> repeatDelay);
    }

    public Duration getRepeatDelay() {
        return this.repeatDelay.get();
    }

    public void resetDelay() {
        this.delay = this.repeatDelay.get().toTicks();
    }

    public abstract void onTriggered() throws CancellationException;

    @Override
    public void run() throws CancellationException {
        this.onTriggered();
        this.resetDelay();
    }
}