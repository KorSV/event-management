package com.ksv.model;

public class Action {

    private final String description;
    private final Runnable runnable;

    public Action(String description, Runnable runnable) {
        this.description = description;
        this.runnable = runnable;
    }

    public String undo() {
        runnable.run();
        return description;
    }

    public String getDescription() {
        return description;
    }
}
