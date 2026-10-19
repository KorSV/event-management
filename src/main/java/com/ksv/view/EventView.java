package com.ksv.view;

import com.ksv.model.Event;
import com.ksv.service.EventService;

import java.time.LocalDateTime;

public class EventView {

    private EventService eventService;

    private InputReader inputReader;

    public EventView(EventService eventService, InputReader inputReader) {
        this.eventService = eventService;
        this.inputReader = inputReader;
    }

    public void showEventDetails(){

    }

    public void createEventHandler(){
        String name = inputReader.getString("Введите имя мероприятия: ");
        int day = inputReader.getInt("Введите дату и время мероприятия: \nдень: ");
        int month = inputReader.getInt("месяц: ");
        int year = inputReader.getInt("год: ");
        int hours = inputReader.getInt("часы (hh24): ");
        int minutes = inputReader.getInt("минуты: ");
        LocalDateTime startsAt = LocalDateTime.of(year, month, day, hours, minutes);
        int maxMembers = inputReader.getInt("Введите максимальное количество участников");
        Event event = eventService.createEvent( name, startsAt, maxMembers);
        System.out.println("Мероприятие добавлено:" + event.toString());

    }

    public void cancelEventHandler(){

    }

    public void registerMemberEventHandler(){
        var idEvent = inputReader.getInt("Введите идентификатор мероприятия: ");
        var idMember = inputReader.getInt("Введите идентификатор участника");
        eventService.registerMember(idEvent, idMember);
    }

    public void unregisterMemberEventHandler(){
        var idEvent = inputReader.getInt("Введите идентификатор мероприятия: ");
        var idMember = inputReader.getInt("Введите идентификатор участника");
        eventService.unregisterMember(idEvent, idMember);
    }

    public void showStatusRegistration(){

    }

}
