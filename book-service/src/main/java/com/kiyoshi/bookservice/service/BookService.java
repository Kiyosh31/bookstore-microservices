package com.kiyoshi.bookservice.service;

import com.kiyoshi.bookservice.entity.dto.BookRequestDto;
import com.kiyoshi.bookservice.entity.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookRequestDto bookRequestDto);
    BookDto getBook(String id);

    List<BookDto> getAllBooks();

}
