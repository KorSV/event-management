package com.ksv.repositories;

import com.ksv.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class UserRepository {
    private int nextId = 1;
    private final Map<Integer,User> users = new HashMap<>();

    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    public boolean existsByName(String name) {
        return users.values().stream().anyMatch(user -> Objects.equals(user.getName(),name));
    }

    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }


}
