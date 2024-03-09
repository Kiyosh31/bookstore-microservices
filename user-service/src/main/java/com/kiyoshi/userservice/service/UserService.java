package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.User;

public interface UserService {
    User createUser(User user);

    User getUser(String id);

    User updateUser(String id, User user);
}
