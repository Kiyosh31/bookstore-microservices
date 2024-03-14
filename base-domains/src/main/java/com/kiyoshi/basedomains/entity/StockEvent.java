package com.kiyoshi.basedomains.entity;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockEvent {
    private String message;
    private String status;
    private Actions action;
    private Stock stock;
    private List<Book> books;
}

