package com.kiyoshi.orderservice.service;

import com.kiyoshi.orderservice.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    Order getOrder(String userId, String orderId);

    List<Order> getAllOrders(String userId);
}
