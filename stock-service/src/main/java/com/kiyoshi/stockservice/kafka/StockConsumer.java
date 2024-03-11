package com.kiyoshi.stockservice.kafka;

import com.kiyoshi.basedomains.entity.Book;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.stockservice.entity.Stock;
import com.kiyoshi.stockservice.exception.ResourceNotFoundException;
import com.kiyoshi.stockservice.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        switch (event.getAction()){
            case CREATE:
                createStock(event);
                break;
            case SUBTRACT:
                subtractStock(event);
                break;
        }
    }

    private void createStock(StockEvent event) {
        // save event data into db
        Stock stock = new Stock();
        stock.setBookId(event.getStock().getBookId());
        stock.setBookName(event.getStock().getBookName());
        stock.setAvailableQuantity(event.getStock().getAvailableQuantity());
        stock.setPrice(event.getStock().getPrice());

        // create register in db
        repository.save(stock);
    }

    private void subtractStock(StockEvent event) {
        // generate a list of bookIds
        List<String> bookIds = event.getBooks().stream().map(Book::getBookId).toList();

        // get a list from db
        Optional<List<Stock>> books = repository.findByBookIdIn(bookIds);
        if(books.isEmpty()) {
            throw new ResourceNotFoundException("No books found", "id", "");
        }

        List<Stock> newStock = books.get();

        // modify the quantity
        for(Book book: event.getBooks()) {
            for(Stock stock: newStock) {
                if(book.getBookId().equals(stock.getBookId())){
                    stock.setAvailableQuantity(stock.getAvailableQuantity() - book.getQuantity());
                }
            }
        }

        // save the list into db
        repository.saveAll(newStock);
    }
}
