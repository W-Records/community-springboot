package com.example.userservice.service.impl;

import com.example.userservice.dao.UserDao;
import com.example.userservice.pojo.User;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;


    @Override
    public void UserRegister(User user) {

    }
}
