package com.kiyoshi.notificationservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;

    private String userId;

    private String title;

    private String description;

    private LocalDateTime createdAt;
}
