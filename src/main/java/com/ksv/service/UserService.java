package com.ksv.service;

import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.User;
import com.ksv.repositories.UserRepository;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private static final int MIN_LENGTH_USERNAME = 3;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id= " + id + " не существует."));
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
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
        if (userRepository.existsByName(name)) {
            throw new ValidationException("Пользователь с таким именем уже существует. Попробуйте другое имя");
        }
        return userRepository.save(new User(name));
    }

}
