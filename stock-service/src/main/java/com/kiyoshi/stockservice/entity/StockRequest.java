package com.kiyoshi.stockservice.entity;

import lombok.Data;

@Data
public class StockRequest {
    private String bookId;
    private Integer quantity;
    private Integer price;
}
