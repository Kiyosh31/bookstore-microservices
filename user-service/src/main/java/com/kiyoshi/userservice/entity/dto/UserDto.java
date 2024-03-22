package com.kiyoshi.userservice.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiyoshi.userservice.entity.collection.Permission;
import com.kiyoshi.userservice.entity.collection.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true, value = {"isActive"})
public class UserDto {
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

    @NotNull(message = "Missing role")
    private String role;

    private Set<Permission> permissions;
}
