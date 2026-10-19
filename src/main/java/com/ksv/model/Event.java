package com.ksv.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event {
    private int id;
    private LocalDateTime startsAt;
    private String name;
    private StatusEvent status;
    private int maxMembers;
    private List<User> members;

    public Event() {
    }

    public Event(int id, String name, LocalDateTime startsAt, int maxMembers) {
        this.id = id;
        this.startsAt = startsAt;
        this.name = name;
        this.maxMembers = maxMembers;
        this.members = new ArrayList<>();
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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public StatusEvent getStatus() {
        return status;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setStatus(StatusEvent status) {
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
