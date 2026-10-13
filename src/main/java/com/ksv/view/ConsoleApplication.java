package com.ksv.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleApplication {
    private Menu menu;
    private Scanner scanner;
    private EventView eventView;
    private boolean running;

    public ConsoleApplication(EventView eventView) {
        this.eventView = eventView;
        this.scanner = new Scanner(System.in);
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
            try {
                int choice = scanner.nextInt();
                handleChoice(choice);
            } catch (InputMismatchException e) {
                System.out.println("Вы ввели некорректное значение. Введите номер из списка.");
                scanner.nextLine();
            }

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
