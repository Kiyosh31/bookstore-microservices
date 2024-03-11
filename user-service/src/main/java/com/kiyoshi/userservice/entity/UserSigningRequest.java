package com.kiyoshi.userservice.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSigningRequest {
    @NotNull(message = "Missing email")
    @Email
    private String email;

    @NotNull(message = "Missing password")
    private String password;
}
