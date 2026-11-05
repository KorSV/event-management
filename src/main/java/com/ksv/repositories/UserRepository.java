package com.ksv.repositories;

import com.ksv.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRepository {
    private int nextId = 1;
    private final List<User> users = new ArrayList<>();

    public Optional<User> findById(int id) {
        return this.users.stream().filter(user -> user.getId() == id).findFirst();
    }

    public List<User> findAll() {
        return List.copyOf(users);
    }

    public boolean existsByName(String name) {
        return users.stream().anyMatch(user -> Objects.equals(user.getName(),name));
    }

    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.add(user);
        return user;
    }


}
