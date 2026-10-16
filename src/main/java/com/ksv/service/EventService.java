package com.ksv.service;

import com.ksv.exceptions.*;
import com.ksv.model.Event;
import com.ksv.model.User;
import com.ksv.model.StatusEvent;
import com.ksv.model.StatusRegistration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        Event event = getEventById(idEvent);
        if (event.getStatus() == StatusEvent.ARCHIVE){
            throw new EventNotFoundException("Мероприятие с таким ID не найдено");
        }
        event.setStatus(StatusEvent.ARCHIVE);
        return event;
    }

    public Event getEventById(int idEvent){
        for (Event event:events){
            if (event.getId() == idEvent){
                return event;
            }
        }
        return null;
    }

    public boolean isUserRegistered(int idEvent, int idUser){
        Event event = getEventById(idEvent);
        if (event == null){
            throw new EventNotFoundException("Мероприятие с таким id не найдено.");
        }
        List<User> eventUsers = event.getMembers();
        for (User user:eventUsers){
            if (user.getId() == idUser){
                return true;
            }
        }
        return false;
    }


    public void registerMember(int idEvent, int idUser){
        Event event = getEventById(idEvent);
        User user = userService.getUserById(idUser);
        if (event == null){
            throw new EventNotFoundException("событие с таким id не найдено");
        }
        if (event.getStatus().equals(StatusEvent.ARCHIVE)){
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
        User user = userService.getUserById(idUser);
        if (user == null){
            throw new UserNotFoundException("Участник с таким id не найден");
        }
        Event event = getEventById(idEvent);
        if (event == null){
            throw new EventNotFoundException("Мероприятие с таким id не найдено");
        }
        if (!isUserRegistered(idEvent, idUser)){
            throw new UserNotRegisteredException("Этот пользователь не зарегистрирован на мероприятие");
        }
        for (User eventUser:event.getMembers()){
            if (eventUser.getId() == idEvent){
                event.getMembers().remove(eventUser);
            }
        }
    }

    public StatusRegistration getStatusRegistration(int eventId, int memberEventId){
        return StatusRegistration.REGISTERED;
    }

    public List<User> getListMember(){
        return List.of();
    }





}
