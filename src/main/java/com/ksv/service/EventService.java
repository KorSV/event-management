package com.ksv.service;

import com.ksv.exceptions.EventAlreadyArchivedException;
import com.ksv.exceptions.CannotRegisterOnArchivedEventException;
import com.ksv.exceptions.EventNotFoundException;
import com.ksv.exceptions.MemberAlreadyRegisterException;
import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.UserNotRegisteredException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.Action;
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
    private final ActionHistoryService actionHistoryService;

    public EventService(EventRepository eventRepository,UserService userService, ActionHistoryService actionHistoryService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.actionHistoryService = actionHistoryService;
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
        Event savedEvent = eventRepository.save(new Event(name, startsAt, maxMembers));
        actionHistoryService.push(new Action(
                "Отменить создание мероприятия id="+ savedEvent.getId(),
                ()->{eventRepository.delete(savedEvent.getId()).orElseThrow(
                        ()->new EventNotFoundException(
                                "Нельзя отменить создание: мероприятие id="+savedEvent.getId()+" не найдено"));}
        ));
        return savedEvent;
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
        actionHistoryService.push(
                new Action("Событие id="+idEvent+" ,было отменено. Хотите вернуть его?",
                () -> {event.setStatus(StatusEvent.ACTUAL);
                        eventRepository.save(event);}
        ));
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
        actionHistoryService.push(
                new Action("Отменить регистрацию пользователя id="+idUser+" на мероприятие id="+idEvent+"?",
                () -> {event.getMembers().removeIf(u -> u.getId() == user.getId());
                event.getWaitList().removeIf(u -> u.getId() == user.getId());
                eventRepository.save(event);
        }));
    }

    public void unregisterMember(int idEvent, int idUser) {
        if (userService.findUserById(idUser).isEmpty()) {
            throw new UserNotFoundException("Пользователь id=" + idUser + " не существует");
        }
        Event event = getEventById(idEvent);
        User removedUser = userService.getUserById(idUser);
        switch (getStatusRegistration(event, idUser)) {
            case REGISTERED -> {
                User promotedUser = event.getWaitList().poll();
                event.getMembers().removeIf(user -> user.getId() == idUser);
                //User member = event.getWaitList().poll();
                if (promotedUser != null) {
                    event.getMembers().add(promotedUser);
                }
                actionHistoryService.push(
                        new Action("отменить отмену регистрации пользователя id=?"+idUser,
                                ()->{
                                    undoUnregisterMember(event, removedUser, promotedUser);
                                })
                        );
            }
            case WAITLIST -> {
                int indexRemovedUser = event.getWaitList().indexOf(removedUser);
                event.getWaitList().removeIf(user -> user.getId() == idUser);
                System.out.println();
                actionHistoryService.push(
                        new Action("Вернуть пользователя id="+ idUser+" в лист ожидания?",
                        () -> event.getWaitList().add(indexRemovedUser,removedUser)));
            }
            case NOT_REGISTERED -> {
                throw new UserNotRegisteredException("Этот пользователь не зарегистрирован на мероприятие");
            }
        }
        eventRepository.save(event);
    }

    private void undoUnregisterMember(Event event, User removedUser, User promotedUser) {
        if (promotedUser != null){
            boolean promotedWasMembers = event.getMembers().removeIf(user -> user.getId() == promotedUser.getId());
            if (promotedWasMembers){
                event.getWaitList().addFirst(promotedUser);
            }
        }
        event.getMembers().add(removedUser);
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
