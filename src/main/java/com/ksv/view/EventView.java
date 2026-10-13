package com.ksv.view;

import com.ksv.service.EventService;

public class EventView {

    private EventService eventService;

    public EventView(EventService eventService) {
        this.eventService = eventService;
    }

    public void showEventDetails(){

    }

    public void createEventHandler(){
        //пока заглушка
        System.out.println("create event handler");
        eventService.createEvent();
    }

    public void cancelEventHandler(){

    }

    public void registerMemberEventHandler(){
        //пока заглушка
        System.out.println("registration of member handler");
        eventService.registerMember();
    }

    public void unregisterMemberEventHandler(){
        //пока заглушка
        System.out.println("unregistration of member handler");
        eventService.unregisterMember();
    }

    public void showStatusRegistration(){

    }

}
