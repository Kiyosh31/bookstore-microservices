package com.kiyoshi.commonutils.entity.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String id;

    private String userId;

    private String title;

    private String description;

    private LocalDateTime createdAt;
}
