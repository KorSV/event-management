package com.ksv;

import com.ksv.service.EventService;
import com.ksv.view.ConsoleApplication;
import com.ksv.view.EventView;
import com.ksv.view.InputReader;

public class Main {
    public static void main(String[] args) {
        ConsoleApplication application = new ConsoleApplication(new EventView(new EventService(), new InputReader()));
        application.run();
    }
}
