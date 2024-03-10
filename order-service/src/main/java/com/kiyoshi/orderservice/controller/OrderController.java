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

    @GetMapping("/")
    public ResponseEntity<Order> getOrder(@RequestParam String userId, @RequestParam String orderId) {
        return new ResponseEntity<>(service.getOrder(userId, orderId), HttpStatus.FOUND);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<Order>> getAllOrders(@PathVariable String userId) {
        return new ResponseEntity<>(service.getAllOrders(userId), HttpStatus.FOUND);
    }
}
