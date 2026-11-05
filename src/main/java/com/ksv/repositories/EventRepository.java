package com.ksv.repositories;

import com.ksv.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepository {
    private int nextId = 1;
    private final List<Event> events = new ArrayList<>();

    public Optional<Event> findById(int id) {
        return events.stream()
                .filter(event -> event.getId() == id).findFirst();
    }

    public List<Event> findAll() {
        return List.copyOf(events);
    }

    public Event save(Event event) {
        if (event.getId() == 0) {
            event.setId(nextId++);
        }
        events.add(event);
        return event;
    }


}
