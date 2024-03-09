package com.kiyoshi.stockservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stock")
public class Stock {
    @Id
    private String id;
    private String bookId;
    private String bookName;
    private int quantity;
}
