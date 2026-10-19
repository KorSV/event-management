package com.ksv.service;

import com.ksv.exceptions.*;
import com.ksv.model.Event;
import com.ksv.model.User;
import com.ksv.model.StatusEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventService {

    private final UserService userService;
    private final List<Event> events = new ArrayList<>();
    private int nextId = 1;

    public EventService(UserService userService) {
        this.userService = userService;
    }

    public Event createEvent(String name, LocalDateTime startsAt, int maxMembers){
        Event event = new Event(nextId++, name, startsAt, maxMembers);
        events.add(event);
        return event;
    }

    public Event cancelEvent(int idEvent){
        Event event = findEventById(idEvent).orElseThrow(() -> new EventNotFoundException("Мероприятие с ID="+idEvent+" не найдено"));
        if (event.getStatus() == StatusEvent.ARCHIVE){
            throw new EventAlreadyArchivedException("Мероприятие id="+event.getId()+" уже находится в архиве.");
        }
        event.setStatus(StatusEvent.ARCHIVE);
        return event;
    }

    public void registerMember(int idEvent, int idUser){
        User user = userService.getUserById(idUser);
        Event event = getEventById(idEvent);
        if (event.getStatus() == StatusEvent.ARCHIVE){
            throw new EventArchivedException("Нельзя записаться на архивное мероприятие");
        }
        if (event.getMembers().size() == event.getMaxMembers()){
            throw new EventFullException("Нет свободных мест");
        }
        if (event.getMembers().contains(user)){
            throw new MemberAlreadyRegisterException("Этот участник уже зарегистрирован на это мероприятие");
        }
        event.getMembers().add(user);
    }

    public void unregisterMember(int idEvent, int idUser){
        if (userService.findUserById(idUser).isEmpty()){
            throw new UserNotFoundException("Пользователь id="+ idUser +"не существует");
        }
        Event event = getEventById(idEvent);
        if (!isUserRegistered(idEvent, idUser)){
            throw new UserNotRegisteredException("Этот пользователь не зарегистрирован на мероприятие");
        }
        event.getMembers().removeIf(user -> user.getId() == idUser);
    }

    /*TODO: вспомогательные методы подлежащие переносу в repository*/
    public Event getEventById(int idEvent){
        return events.stream()
                .filter((event)-> event.getId() == idEvent )
                .findFirst()
                .orElseThrow(()-> new EventNotFoundException("Мероприятие с id= " + idEvent + " не существует." ));
    }

    public Optional<Event> findEventById(int idEvent){
        return events.stream().filter(event -> event.getId() == idEvent).findFirst();
    }

    public boolean isUserRegistered(int idEvent, int idUser){
        Event event = getEventById(idEvent);
        return event.getMembers().stream().anyMatch((user)->user.getId()==idUser);
    }

    public List<User> getListMember(int idEvent){
        return List.copyOf(getEventById(idEvent).getMembers());
    }





}
