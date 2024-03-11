package com.kiyoshi.notificationservice.service.impl;

import com.kiyoshi.notificationservice.entity.Notification;
import com.kiyoshi.notificationservice.exception.ResourceNotFoundException;
import com.kiyoshi.notificationservice.repository.NotificationRepository;
import com.kiyoshi.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository repository;

    @Override
    public List<Notification> getAllUserNotifications(String userId) {
        Optional<List<Notification>> found = repository.findByUserId(userId);

        if(found.isEmpty()){
            throw new ResourceNotFoundException("No notifications found", "userId", userId);
        }

        return found.get();
    }
}
