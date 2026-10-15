package com.ksv.model;

import java.time.LocalDateTime;
import java.util.List;

public class Event {
    private int id;
    private LocalDateTime startsAt;
    private String name;
    private StatusEvent status;
    private int maxMembers;
    private List<MemberEvent> members;

    public Event() {
    }

    public Event(int id, String name, LocalDateTime startsAt, int maxMembers) {
        this.id = id;
        this.startsAt = startsAt;
        this.name = name;
        this.maxMembers = maxMembers;
        this.status = StatusEvent.ACTUAL;
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

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", startsAt=" + startsAt +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", maxMembers=" + maxMembers +
                ", members=" + members +
                '}';
    }
}
