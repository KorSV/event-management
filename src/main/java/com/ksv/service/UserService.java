package com.ksv.service;

import com.ksv.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    private List<User> users = new ArrayList<>();

    public User getUserById(int id){
        for(User user:users){
            if (user.getId() == id){
                return user;
            }
        }
        return null;
    }

}
