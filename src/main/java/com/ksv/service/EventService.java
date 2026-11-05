package com.ksv.service;

import com.ksv.exceptions.EventAlreadyArchivedException;
import com.ksv.exceptions.CannotRegisterOnArchivedEventException;
import com.ksv.exceptions.EventNotFoundException;
import com.ksv.exceptions.MemberAlreadyRegisterException;
import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.UserNotRegisteredException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.Event;
import com.ksv.model.StatusEvent;
import com.ksv.model.StatusRegistration;
import com.ksv.model.User;
import com.ksv.repositories.EventRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class EventService {

    private static final int MIN_MEMBERS_LIMIT = 5;
    private static final int MAX_MEMBERS_LIMIT = 1000;
    private static final LocalTime EVENT_START_MIN = LocalTime.of(9, 0);
    private static final LocalTime EVENT_START_MAX = LocalTime.of(20, 0);
    private final UserService userService;
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository,UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public Event createEvent(String name, LocalDateTime startsAt, int maxMembers) {
        /* validation block*/
        if (name == null || name.isBlank()) {
            throw new ValidationException("Поле наименование мероприятия не может быть пустым");
        }
        if (maxMembers < MIN_MEMBERS_LIMIT) {
            throw new ValidationException(String.format(
                    "Максимальное количество участников мероприятия не может быть меньше %s",
                    MIN_MEMBERS_LIMIT));
        }
        if (maxMembers > MAX_MEMBERS_LIMIT) {
            throw new ValidationException(String.format(
                    "Максимальное количество участников мероприятия не может быть больше %s",
                    MAX_MEMBERS_LIMIT));
        }
        if (startsAt.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Введенная вами дата мероприятия уже прошла");
        }
        if (startsAt.toLocalTime().isBefore(EVENT_START_MIN) || startsAt.toLocalTime().isAfter(EVENT_START_MAX)) {
            throw new ValidationException(String.format(
                    "Мероприятия могут проходить только в рабочее время c %s - %s",
                    EVENT_START_MIN,
                    EVENT_START_MAX));
        }
        return eventRepository.save(new Event(name, startsAt, maxMembers));
    }

    public Event cancelEvent(int idEvent) {
        Event event = findEventById(idEvent)
                .orElseThrow(() -> new EventNotFoundException(
                        "Мероприятие с ID=" + idEvent + " не найдено"));
        if (event.getStatus() == StatusEvent.ARCHIVE) {
            throw new EventAlreadyArchivedException("Мероприятие id=" + event.getId() + " уже находится в архиве.");
        }
        event.setStatus(StatusEvent.ARCHIVE);
        eventRepository.save(event);
        return event;
    }

    public void registerMember(int idEvent, int idUser) {
        Event event = getEventById(idEvent);
        User user = userService.getUserById(idUser);
        if (event.getStatus() == StatusEvent.ARCHIVE) {
            throw new CannotRegisterOnArchivedEventException("Нельзя записаться на архивное мероприятие");
        }
        if (getStatusRegistration(event, idUser) != StatusRegistration.NOT_REGISTERED) {
            throw new MemberAlreadyRegisterException("Этот участник уже зарегистрирован на это мероприятие");
        }
        if (event.getMembers().size() == event.getMaxMembers()) {
            event.getWaitList().offer(user);
        } else {
            event.getMembers().add(user);
        }
        eventRepository.save(event);
    }

    public void unregisterMember(int idEvent, int idUser) {
        if (userService.findUserById(idUser).isEmpty()) {
            throw new UserNotFoundException("Пользователь id=" + idUser + " не существует");
        }
        Event event = getEventById(idEvent);
        switch (getStatusRegistration(event, idUser)) {
            case REGISTERED -> {
                event.getMembers().removeIf(user -> user.getId() == idUser);
                User member = event.getWaitList().poll();
                if (member != null) {
                    event.getMembers().add(member);
                }
            }
            case WAITLIST -> {
                event.getWaitList().removeIf(user -> user.getId() == idUser);
            }
            case NOT_REGISTERED -> {
                throw new UserNotRegisteredException("Этот пользователь не зарегистрирован на мероприятие");
            }
        }
        eventRepository.save(event);
    }

    public Event getEventById(int idEvent) {
        return eventRepository.findById(idEvent)
                .orElseThrow(() -> new EventNotFoundException("Мероприятие с id= " + idEvent + " не существует."));
    }

    public Optional<Event> findEventById(int idEvent) {
        return eventRepository.findById(idEvent);
    }


    public StatusRegistration getStatusRegistration(Event event, int idUser) {
        if (event.getMembers().stream().anyMatch((user) -> user.getId() == idUser)) {
            return StatusRegistration.REGISTERED;
        } else if (event.getWaitList().stream().anyMatch((user) -> user.getId() == idUser)) {
            return StatusRegistration.WAITLIST;
        }
        return StatusRegistration.NOT_REGISTERED;
    }

}
