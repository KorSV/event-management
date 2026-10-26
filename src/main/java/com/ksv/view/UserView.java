package com.ksv.view;

import com.ksv.exceptions.DomainException;
import com.ksv.model.User;
import com.ksv.service.UserService;

import java.util.List;

public class UserView {
    private InputReader inputReader;
    private UserService userService;

    public UserView(InputReader inputReader, UserService userService) {
        this.inputReader = inputReader;
        this.userService = userService;
    }

    public void createUserHandler() {
        String name = inputReader.getString("Введите имя пользователя: ");
        try {
            User user = userService.createUser(name);
            System.out.println(String.format(
                    "Пользователь %s добавлен в систему c id = %d",
                    user.getName(),
                    user.getId()));
        } catch (DomainException e) {
            System.out.println("Ошибка создания пользователя: " + e.getMessage());
        }
    }

    public void showUsersHandler() {
        List<User> users = userService.getUsers();
        for (User user : users) {
            System.out.println(user.toString());
        }

    }
}
