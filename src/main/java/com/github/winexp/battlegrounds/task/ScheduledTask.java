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
                try {
                    this.preTriggerRunnable.run();
                    super.getRunnable().run();
                } catch (TaskCancelledException e) {
                    e.ensureNotAbsolute();
                }
                throw new TaskCancelledException(false);
            }
        };

        this.delay = delay;
    }

    public long getDelay() {
        return this.delay;
    }

    @Override
    public Runnable getRunnable() {
        return this.fixedRunnable;
    }
}