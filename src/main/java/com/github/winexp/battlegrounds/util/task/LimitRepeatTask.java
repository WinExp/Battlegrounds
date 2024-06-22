package com.github.winexp.battlegrounds.util.task;

import java.util.concurrent.CancellationException;

public abstract class LimitRepeatTask extends RepeatTask {
    public static final LimitRepeatTask NONE_TASK = new LimitRepeatTask(-1, -1, -1) {
        @Override
        public void onTriggered() throws CancellationException {
        }

        @Override
        public void onCompleted() throws CancellationException {
        }
    };
    private int count;

    public LimitRepeatTask(int delayTicks, int repeatDelayTicks, int count) {
        super(delayTicks, repeatDelayTicks);
        this.count = count;
        if (count <= 0) {
            this.cancel();
        }
    }

    public abstract void onTriggered() throws CancellationException;

    public abstract void onCompleted() throws CancellationException;

    @Override
    public void run() throws CancellationException {
        if (this.count <= 0) {
            this.onCompleted();
            this.cancel();
            throw new CancellationException();
        }
        super.run();
        this.count--;
    }

    public int getCount() {
        return this.count;
    }
}
