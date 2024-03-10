package com.kiyoshi.stockservice.controller;

import com.kiyoshi.stockservice.entity.StockRequest;
import com.kiyoshi.stockservice.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    @Autowired
    private StockService service;

    @PostMapping("/isStockAvailable")
    public ResponseEntity<Boolean> getIsStockAvailable(@RequestBody List<StockRequest> request) {
        return new ResponseEntity<>(service.getIsStockAvailable(request), HttpStatus.FOUND);
    }
}