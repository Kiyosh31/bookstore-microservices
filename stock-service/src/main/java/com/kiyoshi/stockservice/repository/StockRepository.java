package com.kiyoshi.stockservice.repository;

import com.kiyoshi.stockservice.entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<List<Stock>> findByBookIdIn(List<String> bookIds);
}
