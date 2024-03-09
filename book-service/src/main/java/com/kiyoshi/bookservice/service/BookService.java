package com.kiyoshi.bookservice.service;

import com.kiyoshi.bookservice.entity.Book;

import java.util.List;

public interface BookService {
    Book createBook(Book book);
    Book getBook(String id);

    List<Book> getAllBooks();

}
