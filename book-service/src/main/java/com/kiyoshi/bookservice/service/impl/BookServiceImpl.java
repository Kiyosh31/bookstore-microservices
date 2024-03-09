package com.kiyoshi.bookservice.service.impl;

import com.kiyoshi.basedomains.entity.Stock;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.bookservice.entity.Book;
import com.kiyoshi.bookservice.entity.BookRequest;
import com.kiyoshi.bookservice.exception.ResourceAlreadyExistException;
import com.kiyoshi.bookservice.exception.ResourceNotFoundException;
import com.kiyoshi.bookservice.kafka.StockProducer;
import com.kiyoshi.bookservice.repository.BookRepository;
import com.kiyoshi.bookservice.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository repository;

    @Autowired
    private StockProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(StockEvent.class);


    @Override
    public Book createBook(BookRequest book) {
        Optional<Book> found = repository.finByName(book.getBook().getName());

        if(found.isPresent()) {
            throw new ResourceAlreadyExistException("Book already exists");
        }

        // save the new book to db
        Book createdBook = repository.save(book.getBook());

        // create event
        StockEvent orderEvent = new StockEvent();
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("Order status is pending state");

        Stock stock = new Stock();
        stock.setBookId(createdBook.getId());
        stock.setBookName(createdBook.getName());
        stock.setQuantity(book.getQuantity());
        orderEvent.setStock(stock);

        // dispatch event
        producer.sendMessage(orderEvent);
        LOGGER.info(String.format("Stock event send from book service => %s", orderEvent.toString()));

        return createdBook;
    }

    @Override
    public Book getBook(String id) {
        return findBookById(id);
    }

    @Override
    public List<Book> getAllBooks() {
        return null;
    }

    private Book findBookById(String id) {
        Optional<Book> found = repository.findById(id);

        if(found.isEmpty()) {
            throw new ResourceNotFoundException("Book not found", "id", id);
        }

        return found.get();
    }
}
