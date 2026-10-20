package com.ksv.view;

import com.ksv.exceptions.DomainException;
import com.ksv.exceptions.EventNotFoundException;
import com.ksv.exceptions.UserNotFoundException;
import com.ksv.model.Event;
import com.ksv.service.EventService;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class EventView {

    private static final Predicate<Integer> DAY_VALIDATOR = d -> d > 0 && d < 32;
    private static final Predicate<Integer> MONTH_VALIDATOR = m -> m > 0 && m < 13;
    private static final Predicate<Integer> YEAR_VALIDATOR = y -> y > LocalDate.now().getYear()-1 && y < LocalDate.now().getYear() + 1;
    private static final Predicate<Integer> HOUR_VALIDATOR = h -> h > -1 && h < 25;
    private static final Predicate<Integer> MINUTE_VALIDATOR = mi -> mi > -1 && mi < 60;
    private static final Predicate<Integer> MAX_MEMBERS_OF_EVENT_VALIDATOR = m -> m > 0;

    private EventService eventService;

    private InputReader inputReader;

    public EventView(EventService eventService, InputReader inputReader) {
        this.eventService = eventService;
        this.inputReader = inputReader;
    }

    public void showEventDetails() {

    }

    public void createEventHandler() {

        String name = inputReader.getString("Введите имя мероприятия: ");
        LocalDateTime startsAt = null;
        while (true) {
            try {
                int day = inputReader.getInt("Введите дату и время мероприятия: \nдень: ", DAY_VALIDATOR,
                        "День должен быть в интервале от 1 до 31.");
                int month = inputReader.getInt("месяц: ", MONTH_VALIDATOR,
                        "Месяц должен быть в интервале от 1 до 12.");
                int year = inputReader.getInt("год: ", YEAR_VALIDATOR,
                        "год должен быть в диапазоне от 2026 до 2100");
                int hours = inputReader.getInt("часы (hh24): ", HOUR_VALIDATOR,
                        "Поле час должно быть в интервале 0 до 23");
                int minutes = inputReader.getInt("минуты: ", MINUTE_VALIDATOR,
                        "Поле минуты должно быть в интервале от 0 до 59");
                startsAt = LocalDateTime.of(year, month, day, hours, minutes);
                break;
            } catch (DateTimeException e) {
                System.out.println("Такой даты не существует. Попробуйте снова.");
            }
        }
        int maxMembers = inputReader.getInt("Введите максимальное количество участников: "
                , MAX_MEMBERS_OF_EVENT_VALIDATOR
                , "Максимальное количество участников должно быть больше нуля.");
        try {
            Event event = eventService.createEvent(name, startsAt, maxMembers);
            System.out.println("Мероприятие добавлено:" + event.toString());
        } catch (DomainException e) {
            System.out.println("Ошибка при создании мероприятия: " + e.getMessage());
        }
    }

    public void cancelEventHandler() {
        try {
            var idEvent = inputReader.getInt("Введите идентификатор мероприятия: ");
            eventService.cancelEvent(idEvent);
            System.out.println(String.format("Мероприятие id= %d переведено в архив", idEvent));
        }catch (DomainException e){
            System.out.println("ошибка" + e.getMessage());
        }
    }

    public void registerMemberEventHandler() {
        try {
            var idEvent = inputReader.getInt("Введите идентификатор мероприятия: ");
            var idMember = inputReader.getInt("Введите идентификатор участника");
            eventService.registerMember(idEvent, idMember);
            System.out.println("Пользователь зарегистрирован");
        } catch (DomainException exception) {
            System.out.println("ошибка" + exception.getMessage());
        }
    }

    public void unregisterMemberEventHandler() {
        try {
            var idEvent = inputReader.getInt("Введите идентификатор мероприятия: ");
            var idMember = inputReader.getInt("Введите идентификатор участника");
            eventService.unregisterMember(idEvent, idMember);
            System.out.println(String.format("Отменена регистрация пользователь с id = %d с мероприятия id=%d", idMember, idEvent));
        } catch (DomainException exception) {
            System.out.println("Ошибка:" + exception.getMessage());
        }
    }

    public void showEventsHandler() {
        List<Event> events = eventService.getEvents();
        for(Event event:events){
            System.out.println(event.toString());
        }
    }

    public void showEventDetailsHandler() {
        int eventId = inputReader.getInt("Введите id мероприятия: ");
        try {
            Event event = eventService.findEventById(eventId).orElseThrow(()-> new EventNotFoundException("Мероприятие с таким id не существует"));
            System.out.println(event.toString());
        }catch (DomainException e){
            System.out.println("Ошибка: "+e.getMessage());
        }


    }
}
