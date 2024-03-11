package com.kiyoshi.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaNotificationTopicConfig {

    @Value("${spring.kafka.topic.notification-topic-name}")
    private String notificationTopicName;

    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(notificationTopicName).build();
    }

}
