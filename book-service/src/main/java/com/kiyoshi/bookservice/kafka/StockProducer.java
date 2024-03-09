package com.kiyoshi.bookservice.kafka;

import com.kiyoshi.basedomains.entity.StockEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class StockProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockProducer.class);
    private final NewTopic topic;

    private final KafkaTemplate<String, StockEvent> kafkaTemplate;

    public StockProducer(NewTopic topic, KafkaTemplate<String, StockEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(StockEvent event) {
        LOGGER.info(String.format("Order event => %s", event.toString()));

        Message<StockEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();

        kafkaTemplate.send(message);
    }
}
