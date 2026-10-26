package com.ksv.service;

import com.ksv.exceptions.EventAlreadyArchivedException;
import com.ksv.exceptions.EventArchivedException;
import com.ksv.exceptions.EventFullException;
import com.ksv.exceptions.EventNotFoundException;
import com.ksv.exceptions.MemberAlreadyRegisterException;
import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.UserNotRegisteredException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.Event;
import com.ksv.model.StatusEvent;
import com.ksv.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventServiceTest {

    private EventService eventService;
    private UserService userService;

    @BeforeEach
    void init() {
        this.userService = new UserService();
        this.eventService = new EventService(this.userService);

    }

    @Test
    void getEvents_shouldReturnEmptyList_whenNoEvents() {
        assertThat(eventService.getEvents()).isNotNull().isEmpty();
    }

    @Test
    void getEvents_shouldReturnEventList_whenUsersExist() {
        eventService.createEvent("event1"
                , LocalDateTime.now().plusDays(1)
                , 10);
        eventService.createEvent("event2"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThat(eventService.getEvents()).extracting(Event::getName)
                .containsExactly("event1", "event2");
    }


    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void createEvent_shouldThrow_whenNameIsBlank(String name) {
        assertThatThrownBy(() -> eventService
                .createEvent(name, LocalDateTime.now(), 5))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Поле наименование мероприятия не может быть пустым");
    }

    @Test
    void createEvent_shouldThrow_whenMaxUsersTooSmall() {
        assertThatThrownBy(() -> eventService
                .createEvent("test event", LocalDateTime.now(), 4))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Максимальное количество участников мероприятия не может быть меньше");
    }

    @Test
    void createEvent_shouldThrow_whenMaxUsersTooLong() {
        assertThatThrownBy(() -> eventService
                .createEvent("test event", LocalDateTime.now(), 1001))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Максимальное количество участников мероприятия не может быть больше");
    }

    @Test
    void createEvent_shouldThrow_whenStartsAtHasAlreadyPassed() {
        assertThatThrownBy(
                () -> eventService.createEvent("Тест", LocalDateTime.now().minusDays(1), 5))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Введенная вами дата мероприятия уже прошла");
    }

    @Test
    void createEvents_shouldThrow_whenStartAtNotWorkingTime() {
        assertThatThrownBy(() ->
                eventService.createEvent("test event"
                        , LocalDateTime.of(LocalDate.now().plusDays(1)
                                , LocalTime.of(4, 0))
                        , 5))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Мероприятия могут проходить только в рабочее время c");
    }

    @Test
    void createEvent_shouldAddEventToEventList() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThat(eventService.getEvents()).containsExactly(event);
    }


    @Test
    void cancelEvent_shouldSetArchiveStatusToEvent() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThat(eventService.cancelEvent(event.getId()).getStatus())
                .isEqualTo(StatusEvent.ARCHIVE);
    }

    @Test
    void cancelEvent_shouldThrow_whenEventAlreadyArchive() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        eventService.cancelEvent(event.getId());
        assertThatThrownBy(
                () -> eventService.cancelEvent(event.getId()))
                .isInstanceOf(EventAlreadyArchivedException.class)
                .hasMessageContaining("Мероприятие id=" + event.getId() + " уже находится в архиве.");

    }

    @Test
    void registerMember_shouldAddUserToEventMember() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("admin");
        eventService.registerMember(event.getId(), user.getId());
        assertThat(eventService.getEventById(event.getId()).getMembers()).containsExactly(user);
    }

    @Test
    void registerMember_shouldThrow_whenEventIsArchive() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        eventService.cancelEvent(event.getId());
        User user = userService.createUser("admin");
        assertThatThrownBy(() -> eventService.registerMember(event.getId(), user.getId()))
                .isInstanceOf(EventArchivedException.class)
                .hasMessageContaining("Нельзя записаться на архивное мероприятие");

    }

    @Test
    void registerMember_shouldThrow_whenEventIsFull() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 5);
        for (int i = 0; i < 5; i++) {
            eventService.registerMember(event.getId(),
                    userService.createUser("user" + i).getId());
        }
        assertThatThrownBy(() -> eventService
                .registerMember(event.getId(), userService.createUser("admin").getId()))
                .isInstanceOf(EventFullException.class)
                .hasMessageContaining("Нет свободных мест");
    }

    @Test
    void registerMember_shouldThrow_whenUserAlreadyRegisterOnThisEvent() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("admin");
        eventService.registerMember(event.getId(), user.getId());
        assertThatThrownBy(() -> eventService.registerMember(event.getId(), user.getId()))
                .isInstanceOf(MemberAlreadyRegisterException.class)
                .hasMessageContaining("Этот участник уже зарегистрирован на это мероприятие");

    }

    @Test
    void unregisterMember_shouldThrow_whenEventDoesNotExist() {
        var user = userService.createUser("user");
        assertThatThrownBy(() -> eventService.unregisterMember(999, user.getId()))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие с id= 999 не существует.");
    }

    @Test
    void unregisterMember_shouldThrow_whenUserDoesNotExist() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThatThrownBy(() -> eventService.unregisterMember(event.getId(), 999))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь id=999 не существует");
    }

    @Test
    void unregisterMember_shouldThrow_whenUserNotRegisterOnThisEvent() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("user");
        assertThatThrownBy(() -> eventService.unregisterMember(event.getId(), user.getId()))
                .isInstanceOf(UserNotRegisteredException.class)
                .hasMessageContaining("Этот пользователь не зарегистрирован на мероприятие");
    }
    @Test
    void test() {
        int x = 12345;  // ← MagicNumber, должно упасть
    }

    @Test
    void unregisterMember_shouldRemoveUserFormEventMembers() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("user");
        User admin = userService.createUser("admin");
        eventService.registerMember(event.getId(), user.getId());
        eventService.registerMember(event.getId(), admin.getId());
        eventService.unregisterMember(event.getId(), user.getId());
        assertThat(eventService.getEventById(event.getId()).getMembers())
                .extracting(User::getId)
                .contains(admin.getId())
                .doesNotContain(user.getId());
    }

    @Test
    void getEventById_shouldReturnEvent_whenEventExist() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThat(eventService.getEventById(event.getId()).getName())
                .isEqualTo("test event");
    }


    @Test
    void getEventById_shouldThrow_whenEventDoesNotExist() {
        assertThatThrownBy(() -> eventService.getEventById(999))
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining("Мероприятие с id= 999 не существует.");
    }

    @Test
    void findEventById_shouldReturnEvent_whenEventExist() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThat(eventService.findEventById(event.getId()))
                .hasValueSatisfying(
                        (e) -> assertThat(e.getName()).isEqualTo("test event"));
    }

    @Test
    void findEventById_shouldReturnEmpty_whenEventDoesNotExist() {
        assertThat(eventService.findEventById(999)).isEmpty();
    }

    @Test
    void isUserRegistered_shouldTrue_whenUserRegisteredOnEvent() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("admin");
        eventService.registerMember(event.getId(), user.getId());
        assertThat(eventService.isUserRegistered(event.getId(), user.getId())).isTrue();
    }

    @Test
    void isUserRegistered_shouldFalse_whenUserDoesNotRegisteredOnEvent() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        User user = userService.createUser("admin");
        assertThat(eventService.isUserRegistered(event.getId(), user.getId())).isFalse();
    }

    @Test
    void isUserRegistered_shouldThrow_whenUserDoesNotExist() {
        Event event = eventService.createEvent("test event"
                , LocalDateTime.now().plusDays(1)
                , 10);
        assertThatThrownBy(() -> eventService.isUserRegistered(event.getId(), 999))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void isUserRegistered_shouldThrow_whenEventDoesNotExist() {
        User user = userService.createUser("admin");
        assertThatThrownBy(() -> eventService.isUserRegistered(999, user.getId()))
                .isInstanceOf(EventNotFoundException.class);
    }

}