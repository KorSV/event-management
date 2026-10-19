package com.ksv.service;

import com.ksv.exceptions.UserNotFoundException;
import com.ksv.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private List<User> users = new ArrayList<>();

    public User getUserById(int id){
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(()->new UserNotFoundException("Пользователь с таким " + id + " не существует."));
    }

    public Optional<User> findUserById(int id){
        return users.stream().filter(user-> user.getId() == id).findFirst();
    }

}
