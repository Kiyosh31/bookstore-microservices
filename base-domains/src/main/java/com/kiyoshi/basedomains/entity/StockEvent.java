package com.kiyoshi.basedomains.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockEvent {
    private String message;
    private String status;
    private Stock stock;
}
