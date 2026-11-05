package com.ksv.repositories;

import com.ksv.model.Event;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EventRepository {
    private int nextId = 1;
    private final Map<Integer,Event> events = new HashMap<>();

    public Optional<Event> findById(int id) {
        return Optional.ofNullable(events.get(id));
    }

    public List<Event> findAll() {
        return List.copyOf(events.values());
    }

    public Event save(Event event) {
        if (event.getId() == 0) {
            event.setId(nextId++);
        }
        events.put(event.getId(), event);
        return event;
    }


}
