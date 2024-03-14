package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.dto.DeleteResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto user);

    UserDto getUser(String id);

    UserDto updateUser(String id, UserDto userDto);

    DeleteResponse deleteUser(String id);
}
