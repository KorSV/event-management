package com.ksv.service;
import com.ksv.exceptions.UserNotFoundException;
import com.ksv.exceptions.ValidationException;
import com.ksv.model.User;
import static  org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;


public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void init(){
        userService = new UserService();
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists(){
        User created = userService.createUser("admin");
        User founded = userService.getUserById(created.getId());
        assertThat(founded.getName()).isEqualTo("admin");
        assertThat(founded.getId()).isEqualTo(created.getId());
    }

    @Test
    void getUserById_shouldThrow_whenUserDoesNotExist(){
        assertThatThrownBy(()->userService.getUserById(999))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void findUserById_shouldReturnUser_whenUserExists(){
        User created = userService.createUser("admin");
        assertThat(userService.findUserById(created.getId())).isPresent();
    }

    @Test
    void findUserById_shouldReturnEmpty_whenUserDoesNotExist(){
        assertThat(userService.findUserById(999)).isEmpty();
    }

    @Test
    void getUsers_shouldListUsers_whenUsersExist(){
        userService.createUser("user");
        userService.createUser("admin");
        assertThat(userService.getUsers()).extracting(user -> user.getName())
                .containsExactly("user","admin");
    }

    @Test
    void getUsers_shouldReturnEmptyList_whenNoUsers(){
        List<User> users = userService.getUsers();
        assertThat(users).isNotNull().isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void createUser_shouldThrow_whenNameIsBlank(String name){
        assertThatThrownBy(() -> userService.createUser(name))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Поле имя не может быть пустым");
    }

    @Test
    void createUser_shouldAddUserToListUsers(){
        User created = userService.createUser("admin");
        assertThat(userService.getUsers()).containsExactly(created);
    }

    @Test
    void createUser_shouldThrow_whenUserAlreadyExist(){
        userService.createUser("admin");
        assertThatThrownBy(()->userService.createUser("admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь с таким именем уже существует");
    }

    @Test
    void createUser_shouldThrow_whenUserNameTooShort(){
        assertThatThrownBy(()->userService.createUser("ab"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Имя пользователя не может быть меньше");
    }

}
