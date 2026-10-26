package com.ksv;

import com.ksv.service.EventService;
import com.ksv.service.UserService;
import com.ksv.view.ConsoleApplication;
import com.ksv.view.EventView;
import com.ksv.view.InputReader;
import com.ksv.view.UserView;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader();
        UserService userService = new UserService();
        EventService eventService = new EventService(userService);
        EventView eventView = new EventView(eventService, inputReader);
        UserView userView = new UserView(inputReader, userService);

        ConsoleApplication application = new ConsoleApplication(eventView, userView, inputReader);
        application.run();
    }
}
