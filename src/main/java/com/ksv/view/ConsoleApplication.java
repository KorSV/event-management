package com.ksv.view;

public class ConsoleApplication {
    private Menu menu;
    private InputReader inputReader;
    private EventView eventView;
    private boolean running;

    public ConsoleApplication(EventView eventView, InputReader inputReader) {
        this.eventView = eventView;
        this.inputReader = inputReader;
        init();
    }

    private void init(){
        this.menu = new Menu();
        menu.addItem(1, "Добавить мероприятие");
        menu.addItem(2, "Зарегистрировать участника");
        menu.addItem(3, "Отменить регистрацию участника");
        menu.addItem(4, "Выход");
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
            case 1 -> eventView.createEventHandler();
            case 2 -> eventView.registerMemberEventHandler();
            case 3 -> eventView.unregisterMemberEventHandler();
            case 4 -> stop();
            default -> System.out.println("В меню нет такого пункта. Выберите из списка.");
        }
    }


}
