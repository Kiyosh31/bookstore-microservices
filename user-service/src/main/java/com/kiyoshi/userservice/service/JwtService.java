package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.collection.User;

public interface JwtService {

    String generateToken(String username);

    Boolean validateToken(String token, User user);

    String extractUsername(String token);
}
