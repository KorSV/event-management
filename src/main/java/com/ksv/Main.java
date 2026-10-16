package com.ksv;

import com.ksv.service.EventService;
import com.ksv.service.UserService;
import com.ksv.view.ConsoleApplication;
import com.ksv.view.EventView;
import com.ksv.view.InputReader;

public class Main {
    public static void main(String[] args) {
        InputReader inputReader = new InputReader();
        EventService eventService = new EventService(new UserService());
        EventView eventView = new EventView(eventService, inputReader);

        ConsoleApplication application = new ConsoleApplication(eventView, inputReader);
        application.run();
    }
}
