package com.ksv.service;

import com.ksv.model.Event;
import com.ksv.model.MemberEvent;
import com.ksv.model.StatusRegistration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    private final List<Event> events = new ArrayList<>();

    public Event createEvent(int idEvent, String name, LocalDateTime startsAt, int maxMembers){
        Event event = new Event(idEvent, name, startsAt, maxMembers);
        events.add(event);
        return event;
    }

    public Event cancelEvent(){
        return new Event();
    }

    public Event getEventById(){
        return new Event();
    }

    public void registerMember(){
        //пока заглушка
        System.out.println("process register member");
    }

    public void unregisterMember(){
        //пока заглушка
        System.out.println("process unregister member");
    }

    public StatusRegistration getStatusRegistration(int eventId, int memberEventId){
        return StatusRegistration.REGISTERED;
    }

    public List<MemberEvent> getListMember(){
        return List.of();
    }





}
