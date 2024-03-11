package com.kiyoshi.notificationservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;

    @NotNull(message = "Missing userId")
    private String userId;

    @NotNull(message = "Missing title")
    private String title;

    @NotNull(message = "Missing description")
    private String description;

    private LocalDateTime createdAt;
}
