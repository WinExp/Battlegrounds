package com.github.winexp.battlegrounds.helper.task;

public class RunnableCancelledException extends RuntimeException{
    private final boolean enforceCancel;

    public RunnableCancelledException(){
        this(true);
    }

    public RunnableCancelledException(boolean enforceCancel){
        super();
        this.enforceCancel = enforceCancel;
    }

    public boolean getEnforceCancel(){
        return enforceCancel;
    }
}
