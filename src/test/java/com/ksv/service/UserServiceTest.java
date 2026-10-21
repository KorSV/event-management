package com.ksv.service;

import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class UserServiceTest {
    UserService userService;

    @BeforeEach
    void init(){
        userService = new UserService();
    }

    @Test
    void createUser(){
        Assertions.assertThrows(ValidationException.class, ()-> userService.createUser("") );
    }
}
