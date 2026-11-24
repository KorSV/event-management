package com.ksv.service;

import com.ksv.exceptions.EventNotFoundException;
import com.ksv.exceptions.NoActionToUndoException;
import com.ksv.model.Event;
import com.ksv.model.StatusEvent;
import com.ksv.model.User;
import com.ksv.repositories.EventRepository;
import com.ksv.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

public class ActionHistoryServiceTest {

    private EventService eventService;
    private EventRepository eventRepository;
    private UserService userService;
    private ActionHistoryService actionHistoryService;
    private LocalDateTime validStart;

    @BeforeEach
    void init() {
        eventRepository = new EventRepository();
        UserRepository userRepository = new UserRepository();
        userService = new UserService(userRepository);
        actionHistoryService = new ActionHistoryService();
        eventService = new EventService(eventRepository, userService, actionHistoryService);
        validStart = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void undoUnregisterMember_shouldAddRemovedUserToMembers_whenWaitListWasEmpty() {
        User user1 = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        Event event = eventService.createEvent("event", validStart, 10);
        eventService.registerMember(event.getId(), user1.getId());
        eventService.registerMember(event.getId(), user2.getId());
        assertThat(event.getMembers()).extracting(User::getName).containsExactlyInAnyOrder("user1", "user2");
        eventService.unregisterMember(event.getId(), user1.getId());
        assertThat(event.getMembers()).extracting(User::getName).containsExactlyInAnyOrder("user2");
        actionHistoryService.undoLastAction();
        assertThat(event.getMembers()).extracting(User::getName).containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void undoUnregisterMember_shouldAddRemovedUserToMembersAndAddPromotedUserToWaitList_whenWaitListWasNotEmpty() {
        User user1 = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        User user3 = userService.createUser("user3");
        User user4 = userService.createUser("user4");
        User user5 = userService.createUser("user5");
        User userInWaitList1 = userService.createUser("userInWaitList1");
        User userInWaitList2 = userService.createUser("userInWaitList2");
        Event event = eventService.createEvent("event", validStart, 5);
        int idEvent = event.getId();
        eventService.registerMember(idEvent, user1.getId());
        eventService.registerMember(idEvent, user2.getId());
        eventService.registerMember(idEvent, user3.getId());
        eventService.registerMember(idEvent, user4.getId());
        eventService.registerMember(idEvent, user5.getId());
        eventService.registerMember(idEvent, userInWaitList1.getId());
        eventService.registerMember(idEvent, userInWaitList2.getId());
        eventService.unregisterMember(idEvent, user1.getId());
        assertThat(event).satisfies(e -> {
            assertThat(e.getMembers())
                    .extracting(User::getName)
                    .containsExactlyInAnyOrder("user2", "user3", "user4", "user5", "userInWaitList1");
            assertThat(e.getWaitList()).extracting(User::getName).containsExactly("userInWaitList2");
        });
        actionHistoryService.undoLastAction();
        assertThat(event).satisfies(e -> {
            assertThat(e.getMembers())
                    .extracting(User::getName)
                    .containsExactlyInAnyOrder("user1", "user2", "user3", "user4", "user5");
            assertThat(e.getWaitList())
                    .extracting(User::getName)
                    .containsExactly("userInWaitList1", "userInWaitList2");
        });

    }

    @Test
    void undoUnregisterMember_shouldAddRemovedUserToWaitList_whenUserWasOnWaitList() {
        User user1 = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        User user3 = userService.createUser("user3");
        User user4 = userService.createUser("user4");
        User user5 = userService.createUser("user5");
        User userInWaitList1 = userService.createUser("userInWaitList1");
        User userInWaitList2 = userService.createUser("userInWaitList2");
        Event event = eventService.createEvent("event", validStart, 5);
        int idEvent = event.getId();
        eventService.registerMember(idEvent, user1.getId());
        eventService.registerMember(idEvent, user2.getId());
        eventService.registerMember(idEvent, user3.getId());
        eventService.registerMember(idEvent, user4.getId());
        eventService.registerMember(idEvent, user5.getId());
        eventService.registerMember(idEvent, userInWaitList1.getId());
        eventService.registerMember(idEvent, userInWaitList2.getId());
        assertThat(event.getWaitList())
                .extracting(User::getName)
                .containsExactly("userInWaitList1", "userInWaitList2");
        eventService.unregisterMember(idEvent, userInWaitList1.getId());
        assertThat(event).satisfies(e -> {
            assertThat(e.getMembers()).extracting(User::getName)
                    .containsExactlyInAnyOrder("user1", "user2", "user3", "user4", "user5");
            assertThat(e.getWaitList()).extracting(User::getName)
                    .containsExactly("userInWaitList2");
        });
        actionHistoryService.undoLastAction();
        assertThat(event.getWaitList()).extracting(User::getName)
                .containsExactly("userInWaitList1", "userInWaitList2");
    }

    @Test
    void undoCreateEvent_shouldRemoveEvent_whenEventExists() {
        Event event1 = eventService.createEvent("event1", validStart, 5);
        Event event2 = eventService.createEvent("event2", validStart, 5);
        assertThat(eventService.getEvents())
                .extracting(Event::getName)
                .containsExactlyInAnyOrder("event1", "event2");
        actionHistoryService.undoLastAction();
        assertThat(eventService.getEvents())
                .extracting(Event::getName)
                .containsExactly("event1");
    }


    @Test
    void undoCancelEvent_shouldRestoreActualStatusEvent_whenEventExists() {
        Event event = eventService.createEvent("event", validStart, 5);
        assertThat(eventService.getEventById(event.getId()))
                .extracting(Event::getName)
                .isEqualTo("event");
        eventService.cancelEvent(event.getId());
        assertThat(eventService.getEventById(event.getId()))
                .extracting(Event::getName, Event::getStatus)
                .containsExactly("event", StatusEvent.ARCHIVE);
        actionHistoryService.undoLastAction();
        assertThat(eventService.getEvents())
                .satisfies(events -> {
                    assertThat(events)
                            .hasSize(1);
                    assertThat(events)
                            .extracting(Event::getName, Event::getStatus)
                            .containsExactly(tuple("event", StatusEvent.ACTUAL));
                });
    }

    @Test
    void undoRegisterMember_shouldRemoveFromMembers_whenUserWasAddInMembers() {
        User user1 = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        Event event = eventService.createEvent("event", validStart, 5);
        eventService.registerMember(event.getId(), user1.getId());
        eventService.registerMember(event.getId(), user2.getId());
        assertThat(event.getMembers())
                .extracting(User::getName)
                .containsExactlyInAnyOrder("user1", "user2");
        actionHistoryService.undoLastAction();
        assertThat(event.getMembers())
                .extracting(User::getName)
                .containsExactlyInAnyOrder("user1");
    }

    @Test
    void undoRegisterMember_shouldRemoveFromWaitList_whenUserWasAddInWaitList() {
        User user1 = userService.createUser("user1");
        User user2 = userService.createUser("user2");
        User user3 = userService.createUser("user3");
        User user4 = userService.createUser("user4");
        User user5 = userService.createUser("user5");
        User userInWaitList1 = userService.createUser("userInWaitList1");
        User userInWaitList2 = userService.createUser("userInWaitList2");
        Event event = eventService.createEvent("event", validStart, 5);
        int idEvent = event.getId();
        eventService.registerMember(idEvent, user1.getId());
        eventService.registerMember(idEvent, user2.getId());
        eventService.registerMember(idEvent, user3.getId());
        eventService.registerMember(idEvent, user4.getId());
        eventService.registerMember(idEvent, user5.getId());
        eventService.registerMember(idEvent, userInWaitList1.getId());
        eventService.registerMember(idEvent, userInWaitList2.getId());
        assertThat(event)
                .satisfies(e -> {
                    assertThat(e.getMembers())
                            .extracting(User::getName)
                            .containsExactlyInAnyOrder("user1", "user2", "user3", "user4", "user5");
                    assertThat(e.getWaitList())
                            .extracting(User::getName)
                            .containsExactly("userInWaitList1", "userInWaitList2");

                });
        actionHistoryService.undoLastAction();
        assertThat(event)
                .satisfies(e -> {
                    assertThat(e.getMembers())
                            .extracting(User::getName)
                            .containsExactlyInAnyOrder("user1", "user2", "user3", "user4", "user5");
                    assertThat(e.getWaitList())
                            .extracting(User::getName)
                            .containsExactly("userInWaitList1");

                });

    }


    @Test
    void undoLastAction_shouldUndoOnlyLastAction_whenActionHistoryMoreThanOne() {
        User user = userService.createUser("user");
        Event event = eventService.createEvent("event", validStart, 5);
        eventService.registerMember(event.getId(), user.getId());
        eventService.cancelEvent(event.getId());
        assertThat(eventService.getEventById(event.getId()))
                .satisfies(e -> {
                    assertThat(e.getStatus()).isEqualTo(StatusEvent.ARCHIVE);
                    assertThat(e.getMembers())
                            .extracting(User::getName)
                            .containsExactly("user");
                });
        actionHistoryService.undoLastAction();
        assertThat(eventService.getEventById(event.getId()))
                .satisfies(e -> {
                    assertThat(e.getStatus())
                            .isEqualTo(StatusEvent.ACTUAL);
                    assertThat(e.getMembers())
                            .extracting(User::getName)
                            .containsExactly("user");
                });
    }


    @Test
    void existsLastAction_shouldReturnTrue_whenLastActionNotEmpty() {
        eventService.createEvent("event", validStart, 5);
        assertThat(actionHistoryService.existsLastAction()).isTrue();
    }

    @Test
    void existsLastAction_shouldReturnFalse_whenLastActionIsEmpty() {
        assertThat(actionHistoryService.existsLastAction()).isFalse();
    }

    @Test
    void undoLastAction_shouldThrow_whenActionHistoryIsEmpty() {
        assertThat(actionHistoryService.existsLastAction()).isFalse();
        assertThatThrownBy( () -> actionHistoryService.undoLastAction())
                .isInstanceOf(NoActionToUndoException.class)
                .hasMessageContaining("В истории нет действия для отмены");
    }

    @Test
    void getDescriptionLastAction_shouldReturnDescription_whenLastActionExists() {
        Event event = eventService.createEvent("event", validStart, 5);
        assertThat(actionHistoryService.existsLastAction()).isTrue();
        assertThat(actionHistoryService.getDescriptionLastAction())
                .isEqualTo(String.format("Отменить создание мероприятия id=%s", event.getId()));
    }

    @Test
    void test() {
        Event event = eventService.createEvent("event", validStart, 5);
        eventRepository.delete(event.getId());
        assertThat(actionHistoryService.existsLastAction()).isTrue();
        assertThatThrownBy( () -> actionHistoryService.undoLastAction())
                .isInstanceOf(EventNotFoundException.class)
                .hasMessageContaining(
                        String.format(
                                "Нельзя отменить создание: мероприятие id=%s не найдено",event.getId()));
    }
}
