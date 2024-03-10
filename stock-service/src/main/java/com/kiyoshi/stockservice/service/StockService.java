package com.kiyoshi.stockservice.service;

import com.kiyoshi.stockservice.entity.StockRequest;

import java.util.List;

public interface StockService {
    Boolean getIsStockAvailable(List<StockRequest> request);
}
