package com.ksv.view;

import com.ksv.service.EventService;

import java.time.LocalDateTime;
import java.util.Scanner;

public class EventView {

    private EventService eventService;

    public EventView(EventService eventService) {
        this.eventService = eventService;
    }

    public void showEventDetails(){

    }

    public void createEventHandler(){
        //Здесь нажно считать параметры с клавиатуры и передать в eventService.createEvent
        System.out.println("create event handler");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите id мероприятия: ");
        int idEvent = scanner.nextInt();
        System.out.println("Введите имя мероприятия: ");
        String name = scanner.nextLine();
        System.out.println("Введите дату и время мероприятия: ");
        System.out.println("день: ");
        int day = scanner.nextInt();
        System.out.println("месяц: ");
        int month = scanner.nextInt();
        System.out.println("год: ");
        int year = scanner.nextInt();
        System.out.println("часы (hh24): ");
        int hours = scanner.nextInt();
        System.out.println("минуты: ");
        int minutes = scanner.nextInt();
        LocalDateTime startsAt = LocalDateTime.of(year, month, day, hours, minutes);
        System.out.println("Введите максимальное количество участников");
        int maxMembers = scanner.nextInt();
        eventService.createEvent(idEvent, name, startsAt, maxMembers);
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
