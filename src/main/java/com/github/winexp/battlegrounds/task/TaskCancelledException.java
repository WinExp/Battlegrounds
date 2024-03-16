package com.github.winexp.battlegrounds.task;

public class TaskCancelledException extends RuntimeException {
    private final boolean absolute;

    public TaskCancelledException() {
        this(true);
    }

    public TaskCancelledException(boolean absolute) {
        super();
        this.absolute = absolute;
    }

    public void ensureNotAbsolute() {
        if (this.isAbsolute()) throw this;
    }

    public boolean isAbsolute() {
        return absolute;
    }
}
