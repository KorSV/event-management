package com.ksv;

import com.ksv.repositories.EventRepository;
import com.ksv.repositories.UserRepository;
import com.ksv.service.EventService;
import com.ksv.service.UserService;
import com.ksv.view.ConsoleApplication;
import com.ksv.view.EventView;
import com.ksv.view.InputReader;
import com.ksv.view.UserView;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader();
        UserRepository userRepository = new UserRepository();
        EventRepository eventRepository = new EventRepository();

        UserService userService = new UserService(userRepository);
        EventService eventService = new EventService(eventRepository, userService);
        EventView eventView = new EventView(eventService, inputReader);
        UserView userView = new UserView(inputReader, userService);

        ConsoleApplication application = new ConsoleApplication(eventView, userView, inputReader);
        application.run();
    }
}
