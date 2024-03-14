package com.kiyoshi.basedomains.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    private String id;
    private String bookId;
    private String bookName;
    private Integer availableQuantity;
    private Long price;
}
