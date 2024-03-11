package com.kiyoshi.notificationservice.kafka;

import com.kiyoshi.basedomains.entity.NotificationEvent;
import com.kiyoshi.notificationservice.entity.Notification;
import com.kiyoshi.notificationservice.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {
    @Autowired
    private NotificationRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(NotificationEvent event) {
        LOGGER.info(String.format("Notification event received in notification service => %s", event.toString()));

        //save event to db
        Notification newNotification = new Notification();
        newNotification.setTitle(event.getNotification().getTitle());
        newNotification.setDescription(event.getNotification().getDescription());
        newNotification.setCreatedAt(LocalDateTime.now());
        repository.save(newNotification);

        //send notification to FE
    }
}
