package com.kiyoshi.stockservice.kafka;

import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.stockservice.entity.Stock;
import com.kiyoshi.stockservice.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockConsumer {
    @Autowired
    private StockRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(StockConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(StockEvent event) {
        LOGGER.info(String.format("Stock event received in stock service => %s", event.toString()));

        // save event data into db
        Stock stock = new Stock();
        stock.setBookId(event.getStock().getBookId());
        stock.setBookName(event.getStock().getBookName());
        stock.setQuantity(event.getStock().getQuantity());

        repository.save(stock);
    }
}
