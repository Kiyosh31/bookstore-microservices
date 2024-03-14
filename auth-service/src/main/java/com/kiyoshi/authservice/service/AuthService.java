package com.kiyoshi.authservice.service;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;

public interface AuthService {
    TokenResponse generateToken(TokenRequest request);
    Boolean validateToken(String authHeader);
}
