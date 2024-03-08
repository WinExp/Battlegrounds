package com.github.winexp.battlegrounds.task;

public class ScheduledTask extends AbstractTask {
    public static final ScheduledTask NONE_TASK = new ScheduledTask(AbstractTask.NONE_RUNNABLE, -1);
    private final Runnable fixedRunnable;
    protected long delay;
    protected Runnable preTriggerRunnable = () -> {
    };

    public ScheduledTask(Runnable runnable, long delay) {
        super(runnable);
        fixedRunnable = () -> {
            if (this.delay < 0) throw new TaskCancelledException(true);

            this.delay--;

            if (this.delay <= 0) {
                preTriggerRunnable.run();
                super.run();
                throw new TaskCancelledException(false);
            }
        };

        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    @Override
    public void run() {
        fixedRunnable.run();
    }

    @Override
    public Runnable getRunnable() {
        return fixedRunnable;
    }
}