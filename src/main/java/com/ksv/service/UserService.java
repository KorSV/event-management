package com.ksv.service;

import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private static final int MIN_LENGTH_USERNAME = 3;
    private int nextId = 1;
    private List<User> users = new ArrayList<>();

    public List<User> getUsers() {
        return List.copyOf(users);
    }

    public User getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id= " + id + " не существует."));
    }

    public Optional<User> findUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst();
    }

    public User createUser(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Поле имя не может быть пустым");
        }
        if (name.length() < MIN_LENGTH_USERNAME) {
            throw new ValidationException(String.format(
                    "Имя пользователя не может быть меньше %d символов",
                    MIN_LENGTH_USERNAME));
        }
        if (users.stream().anyMatch(user -> user.getName().equals(name))) {
            throw new ValidationException("Пользователь с таким именем уже существует. Попробуйте другое имя");
        }
        User user = new User(nextId++, name);
        users.add(user);
        return user;
    }

}
