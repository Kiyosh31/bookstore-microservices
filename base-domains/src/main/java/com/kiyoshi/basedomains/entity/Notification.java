package com.kiyoshi.basedomains.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {
    private String id;

    private String userId;

    private String title;

    private String description;

    private LocalDateTime createdAt;
}
