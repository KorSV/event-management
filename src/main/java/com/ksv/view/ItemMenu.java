package com.ksv.view;

public class ItemMenu {

    public static final int ADD_USER = 1;
    public static final int SHOW_USERS = 2;
    public static final int ADD_EVENT = 3;
    public static final int CANCEL_EVENT = 4;
    public static final int SHOW_EVENTS = 5;
    public static final int EVENT_DETAILS = 6;
    public static final int REGISTER_MEMBER = 7;
    public static final int UNREGISTER_MEMBER = 8;
    public static final int EXIT = 0;

    private int id;
    private String title;

    public ItemMenu(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return id + ".) " + title;
    }
}
