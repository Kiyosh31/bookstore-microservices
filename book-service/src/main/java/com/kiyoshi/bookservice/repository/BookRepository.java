package com.kiyoshi.bookservice.repository;

import com.kiyoshi.bookservice.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    @Query("{name: ?0}")
    Optional<Book> finByName(String name);
}
