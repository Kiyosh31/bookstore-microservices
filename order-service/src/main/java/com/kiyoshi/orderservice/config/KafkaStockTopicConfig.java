package com.kiyoshi.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaStockTopicConfig {
    @Value("${spring.kafka.topic.stock-topic-name}")
    private String stockTopicName;


    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder.name(stockTopicName).build();
    }
}
