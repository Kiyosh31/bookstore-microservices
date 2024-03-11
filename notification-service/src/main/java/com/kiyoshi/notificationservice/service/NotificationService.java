package com.kiyoshi.notificationservice.service;

import com.kiyoshi.notificationservice.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllUserNotifications(String userId);
}
