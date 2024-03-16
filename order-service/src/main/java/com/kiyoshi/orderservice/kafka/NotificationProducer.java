package com.kiyoshi.orderservice.kafka;

import com.kiyoshi.commonutils.entity.notification.NotificationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProducer.class);
    private final NewTopic notificationTopic;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducer(NewTopic notificationTopic, KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.notificationTopic = notificationTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(NotificationEvent event) {
        LOGGER.info(String.format("Notification event => %s", event.toString()));

        Message<NotificationEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, notificationTopic.name())
                .build();

        kafkaTemplate.send(message);
    }
}
