package com.kiyoshi.notificationservice.controller;

import com.kiyoshi.notificationservice.entity.Notification;
import com.kiyoshi.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    @Autowired
    private NotificationService service;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getAllUserNotifications(@PathVariable String userId) {
        return new ResponseEntity<>(service.getAllUserNotifications(userId), HttpStatus.FOUND);
    }
}
