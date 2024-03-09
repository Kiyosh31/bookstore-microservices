package com.kiyoshi.bookservice.controller;

import com.kiyoshi.bookservice.entity.Book;
import com.kiyoshi.bookservice.entity.BookRequest;
import com.kiyoshi.bookservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/book")
public class BookController {
    @Autowired
    private BookService service;

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest book){
        return new ResponseEntity<>(service.createBook(book), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return new ResponseEntity<>(service.getBook(id), HttpStatus.FOUND);
    }

    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(service.getAllBooks(), HttpStatus.FOUND);
    }
}
