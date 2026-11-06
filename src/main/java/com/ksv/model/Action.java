package com.ksv.model;

public class Action {
    private final Runnable runnable;

    public Action(Runnable runnable) {
        this.runnable = runnable;
    }

    public void undo() {
        runnable.run();
    }
}
