package com.kiyoshi.notificationservice.repository;

import com.kiyoshi.notificationservice.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<List<Notification>> findByUserId(String userId);
}
