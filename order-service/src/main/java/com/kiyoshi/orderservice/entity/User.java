package com.kiyoshi.orderservice.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class User {
    @NotNull(message = "Missing userId")
    private String id;

    @NotEmpty(message = "Missing name")
    private String name;

    @NotEmpty(message = "Missing email")
    @Email
    private String email;

    @NotEmpty(message = "Missing password")
    private String password;

    @NotNull(message = "Missing card")
    private int card;

    @NotEmpty(message = "Missing role")
    private String role;
}
