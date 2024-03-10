package com.kiyoshi.orderservice.controller;

import com.kiyoshi.orderservice.entity.Order;
import com.kiyoshi.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    private OrderService service;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        return new ResponseEntity<>(service.createOrder(order), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {

        return new ResponseEntity<>(service.getOrder(id), HttpStatus.FOUND);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getAllOrders(@PathVariable String userId) {
        return new ResponseEntity<>(service.getAllOrders(userId), HttpStatus.FOUND);
    }
}
