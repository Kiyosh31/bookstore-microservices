package com.kiyoshi.userservice.entity.collection;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Builder
@Document(collection = "user")
public class User {
    @Id
    private String id;

    private String name;

    private String email;

    private String password;

    private int card;

    private Set<Permission> permissions;

    private Boolean isActive;
}

