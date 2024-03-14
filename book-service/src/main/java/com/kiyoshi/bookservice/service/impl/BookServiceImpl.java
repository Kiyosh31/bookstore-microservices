package com.kiyoshi.bookservice.service.impl;

import com.kiyoshi.basedomains.entity.Actions;
import com.kiyoshi.basedomains.entity.Stock;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.bookservice.entity.collection.Book;
import com.kiyoshi.bookservice.entity.dto.BookRequestDto;
import com.kiyoshi.bookservice.entity.dto.BookDto;
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
    public BookDto createBook(BookRequestDto bookRequestDto) {
        Optional<Book> found = repository.finByName(bookRequestDto.getBook().getName());
        if(found.isPresent()) {
            throw new ResourceAlreadyExistException("Book already exists");
        }

        // map dto to book
        Book newBook = mapDtoToBook(bookRequestDto);
        // save the new book to db
        Book createdBook = repository.save(newBook);

        // create event
        StockEvent event = createStockEvent(createdBook, bookRequestDto.getAvailableQuantity(), bookRequestDto.getPrice());

        // dispatch event
        producer.sendMessage(event);
        LOGGER.info(String.format("Stock event send from book service => %s", event.toString()));

        // map book to dto
        return mapBookToDto(createdBook);
    }

    @Override
    public BookDto getBook(String id) {
        Optional<Book> existingBook = repository.findById(id);
        if(existingBook.isEmpty()) {
            throw new ResourceNotFoundException("Book not found", "id", id);
        }

        return mapBookToDto(existingBook.get());
    }

    @Override
    public List<BookDto> getAllBooks() {
        return null;
    }

    private Book mapDtoToBook(BookRequestDto bookRequestDto) {
        Book book = bookRequestDto.getBook();

        return Book.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .editorial(book.getEditorial())
                .pages(book.getPages())
                .build();
    }

    private BookDto mapBookToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(book.getAuthor())
                .editorial(book.getEditorial())
                .pages(book.getPages())
                .build();
    }

    private StockEvent createStockEvent(Book createdBook, Integer availableQuantity, Long price) {
        return StockEvent.builder()
                .status("PENDING")
                .message("Creating stock in database")
                .action(Actions.CREATE)
                .stock(createStock(
                        createdBook.getId(),
                        createdBook.getName(),
                        availableQuantity,
                        price
                ))
                .build();
    }

    private Stock createStock(String id, String name, Integer availableQuantity, Long price) {
        return Stock.builder()
                .bookId(id)
                .bookName(name)
                .availableQuantity(availableQuantity)
                .price(price)
                .build();
    }
}
