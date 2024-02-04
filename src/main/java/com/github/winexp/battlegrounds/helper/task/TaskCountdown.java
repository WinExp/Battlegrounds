package com.github.winexp.battlegrounds.helper.task;

public class TaskCountdown extends TaskLater{
    public final static TaskCountdown NONE_TASK = new TaskCountdown(Task.NONE_RUNNABLE, Task.NONE_RUNNABLE, -1, 0, -1);
    private final Runnable fixedRunnable;
    private final long unitTicks;
    private int count;

    public TaskCountdown(Runnable trigger, Runnable triggerEnd, long delay, long unitTicks, int count) {
        super(trigger, delay);
        fixedRunnable = () -> {
            if (this.count < 0){
                throw new RunnableCancelledException(true);
            }

            try{
                if (this.count == 0){
                    this.preTriggerRunnable = () -> {
                        triggerEnd.run();
                        throw new RunnableCancelledException(false);
                    };
                }
                super.run();
            }
            catch (RunnableCancelledException e){
                if (e.getEnforceCancel()){
                    throw e;
                }
                this.count--;
                this.delay = this.getUnitTicks();
            }

            if (this.count < 0){
                throw new RunnableCancelledException(false);
            }
        };

        this.count = count;
        this.unitTicks = unitTicks;
    }

    @Override
    public void run(){
        fixedRunnable.run();
    }

    @Override
    public Runnable getRunnable(){
        return fixedRunnable;
    }

    public int getCount(){
        return count;
    }

    public long getUnitTicks(){
        return unitTicks;
    }
}
