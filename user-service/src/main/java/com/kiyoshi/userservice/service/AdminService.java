package com.kiyoshi.userservice.service;

import com.kiyoshi.userservice.entity.dto.DeleteResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;

public interface AdminService {
    UserDto createAdmin(UserDto userDto);

    UserDto getAdmin(String id);

    UserDto updateAdmin(String id, UserDto userDto);

    DeleteResponse deleteAdmin(String id);

    UserDto reactivateAdmin(String id);
}
