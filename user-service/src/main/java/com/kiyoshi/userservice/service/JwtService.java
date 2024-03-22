package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.collection.User;

public interface JwtService {

    String generateToken(User user);

    Boolean validateToken(String token, User user);

    String extractEmail(String token);
}
