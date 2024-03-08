package com.github.winexp.battlegrounds.task;

public class TaskCancelledException extends RuntimeException {
    private final boolean enforceCancel;

    public TaskCancelledException() {
        this(true);
    }

    public TaskCancelledException(boolean enforceCancel) {
        super();
        this.enforceCancel = enforceCancel;
    }

    public boolean getEnforceCancel() {
        return enforceCancel;
    }
}
