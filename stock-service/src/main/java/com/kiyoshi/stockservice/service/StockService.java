package com.kiyoshi.stockservice.service;

import com.kiyoshi.commonutils.Book;
import com.kiyoshi.stockservice.entity.Stock;

import java.util.List;

public interface StockService {
    List<Stock> stockAvailable(List<Book> request);
}
