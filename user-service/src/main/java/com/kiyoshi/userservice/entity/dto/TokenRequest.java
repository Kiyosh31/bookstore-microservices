package com.kiyoshi.userservice.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenRequest {
    @NotNull(message = "Missing email")
    @Email
    private String email;

    @NotNull(message = "Missing password")
    private String password;
}
