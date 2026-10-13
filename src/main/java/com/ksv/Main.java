package com.ksv;

import com.ksv.service.EventService;
import com.ksv.view.ConsoleApplication;
import com.ksv.view.EventView;

public class Main {
    public static void main() {
        ConsoleApplication application = new ConsoleApplication(new EventView(new EventService()));
        application.run();
    }
}
