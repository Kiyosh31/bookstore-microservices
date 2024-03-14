package com.kiyoshi.bookservice.controller;

import com.kiyoshi.bookservice.entity.collection.Book;
import com.kiyoshi.bookservice.entity.dto.BookRequestDto;
import com.kiyoshi.bookservice.entity.dto.BookDto;
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

    @PostMapping("/create")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookRequestDto book){
        return new ResponseEntity<>(service.createBook(book), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable String id) {
        return new ResponseEntity<>(service.getBook(id), HttpStatus.FOUND);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return new ResponseEntity<>(service.getAllBooks(), HttpStatus.FOUND);
    }
}
