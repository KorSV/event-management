package com.ksv.model;

import java.util.List;

public class Event {
    private int id;
    private String name;
    private StatusEvent status;
    private int maxMembers;
    private List<MemberEvent> members;

    public Event() {
    }

    public Event(int id, String name, int maxMembers) {
        this.id = id;
        this.name = name;
        this.maxMembers = maxMembers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MemberEvent> getMembers() {
        return members;
    }

    public void setMembers(List<MemberEvent> members) {
        this.members = members;
    }
}
