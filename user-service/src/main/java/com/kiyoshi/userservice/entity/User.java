package com.kiyoshi.userservice.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user")
public class User {
    @Id
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
