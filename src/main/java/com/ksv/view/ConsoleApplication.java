package com.ksv.view;

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

    private void init(){
        this.menu = new Menu();
        menu.addItem(1, "Добавить сотрудника");
        menu.addItem(2, "Показать участников");
        menu.addItem(3, "Добавить мероприятие");
        menu.addItem(4, "Отменить мероприятие");
        menu.addItem(5, "Список мероприятий");
        menu.addItem(6, "Показать детали мероприятия");
        menu.addItem(7, "Зарегистрировать участника");
        menu.addItem(8, "Отменить регистрацию участника");
        menu.addItem(0, "Выход");
    }

    public void run(){
        this.running = true;
        while (running){
            menu.show();
            handleChoice(inputReader.getInt("Выберите пункт меню: "));
        }
    }

    private void stop(){
        this.running = false;
        System.out.println("До свидания!");
    }

    private void handleChoice(int choice){
        switch (choice){
            case 1 -> userView.createUserHandler();
            case 2 -> userView.showUsersHandler();
            case 3 -> eventView.createEventHandler();
            case 4 -> eventView.cancelEventHandler();
            case 5 -> eventView.showEventsHandler();
            case 6 -> eventView.showEventDetailsHandler();
            case 7 -> eventView.registerMemberEventHandler();
            case 8 -> eventView.unregisterMemberEventHandler();
            case 0 -> stop();
            default -> System.out.println("В меню нет такого пункта. Выберите из списка.");
        }
    }


}
