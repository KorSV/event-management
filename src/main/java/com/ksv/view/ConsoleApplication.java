package com.ksv.view;


import static com.ksv.view.ItemMenu.ADD_EVENT;
import static com.ksv.view.ItemMenu.ADD_USER;
import static com.ksv.view.ItemMenu.CANCEL_EVENT;
import static com.ksv.view.ItemMenu.EVENT_DETAILS;
import static com.ksv.view.ItemMenu.EXIT;
import static com.ksv.view.ItemMenu.REGISTER_MEMBER;
import static com.ksv.view.ItemMenu.SHOW_EVENTS;
import static com.ksv.view.ItemMenu.SHOW_USERS;
import static com.ksv.view.ItemMenu.UNREGISTER_MEMBER;

public class ConsoleApplication {
    private Menu menu;
    private InputReader inputReader;
    private EventView eventView;
    private UserView userView;
    private boolean running;

    public ConsoleApplication(EventView eventView, UserView userView, InputReader inputReader) {
        this.eventView = eventView;
        this.userView = userView;
        this.inputReader = inputReader;
        init();
    }

    private void init() {
        this.menu = new Menu();
        menu.addItem(ADD_USER, "Добавить сотрудника");
        menu.addItem(SHOW_USERS, "Показать участников");
        menu.addItem(ADD_EVENT, "Добавить мероприятие");
        menu.addItem(CANCEL_EVENT, "Отменить мероприятие");
        menu.addItem(SHOW_EVENTS, "Список мероприятий");
        menu.addItem(EVENT_DETAILS, "Показать детали мероприятия");
        menu.addItem(REGISTER_MEMBER, "Зарегистрировать участника");
        menu.addItem(UNREGISTER_MEMBER, "Отменить регистрацию участника");
        menu.addItem(EXIT, "Выход");
    }

    public void run() {
        this.running = true;
        while (running) {
            menu.show();
            handleChoice(inputReader.getInt("Выберите пункт меню: "));
        }
    }

    private void stop() {
        this.running = false;
        System.out.println("До свидания!");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case ADD_USER -> userView.createUserHandler();
            case SHOW_USERS -> userView.showUsersHandler();
            case ADD_EVENT -> eventView.createEventHandler();
            case CANCEL_EVENT -> eventView.cancelEventHandler();
            case SHOW_EVENTS -> eventView.showEventsHandler();
            case EVENT_DETAILS -> eventView.showEventDetailsHandler();
            case REGISTER_MEMBER -> eventView.registerMemberEventHandler();
            case UNREGISTER_MEMBER -> eventView.unregisterMemberEventHandler();
            case EXIT -> stop();
            default -> System.out.println("В меню нет такого пункта. Выберите из списка.");
        }
    }


}
