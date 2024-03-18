package com.kiyoshi.authservice.service;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;

public interface AuthService {
    TokenResponse loginUser(TokenRequest request);

    TokenResponse validateToken(String token);
}
