package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.dto.TokenRequest;
import com.kiyoshi.userservice.entity.dto.TokenResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;

public interface UserService {
    TokenResponse loginUser(TokenRequest request);

    TokenResponse validateToken(String token);

    UserDto createUser(UserDto user);

    UserDto getUser(String id);

    UserDto updateUser(String id, UserDto userDto);

    UserDto deleteUser(String id);

    UserDto reactivateUser(String id);
}
