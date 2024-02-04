package com.github.winexp.battlegrounds.helper.task;

public class TaskLater extends Task{
    public final static TaskLater NONE_TASK = new TaskLater(Task.NONE_RUNNABLE, -1);
    private final Runnable fixedRunnable;
    protected long delay;
    protected Runnable preTriggerRunnable = () -> {};

    public long getDelay() { return delay; }

    public TaskLater(Runnable runnable, long delay){
        super(runnable);
        fixedRunnable = () -> {
            if (this.delay < 0) throw new RunnableCancelledException(true);

            this.delay--;

            if (this.delay <= 0){
                preTriggerRunnable.run();
                super.run();
                throw new RunnableCancelledException(false);
            }
        };

        this.delay = delay;
    }

    @Override
    public void run(){
        fixedRunnable.run();
    }

    @Override
    public Runnable getRunnable(){
        return fixedRunnable;
    }
}