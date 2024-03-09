package com.kiyoshi.bookservice.service;

import com.kiyoshi.bookservice.entity.Book;
import com.kiyoshi.bookservice.entity.BookRequest;

import java.util.List;

public interface BookService {
    Book createBook(BookRequest book);
    Book getBook(String id);

    List<Book> getAllBooks();

}
