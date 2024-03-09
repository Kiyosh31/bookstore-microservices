package com.kiyoshi.bookservice.service.impl;

import com.kiyoshi.bookservice.entity.Book;
import com.kiyoshi.bookservice.exception.ResourceAlreadyExistException;
import com.kiyoshi.bookservice.exception.ResourceNotFoundException;
import com.kiyoshi.bookservice.repository.BookRepository;
import com.kiyoshi.bookservice.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository repository;


    @Override
    public Book createBook(Book book) {
        Optional<Book> found = repository.finByName(book.getName());

        if(found.isPresent()) {
            throw new ResourceAlreadyExistException("Book already exists");
        }

        // save the new book to db
        return repository.save(book);

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
