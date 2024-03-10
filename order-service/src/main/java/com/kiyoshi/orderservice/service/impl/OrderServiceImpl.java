package com.kiyoshi.orderservice.service.impl;

import com.kiyoshi.basedomains.entity.Actions;
import com.kiyoshi.basedomains.entity.Book;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.orderservice.entity.Order;
import com.kiyoshi.orderservice.entity.User;
import com.kiyoshi.orderservice.exception.ResourceNotFoundException;
import com.kiyoshi.orderservice.kafka.StockProducer;
import com.kiyoshi.orderservice.repository.OrderRepository;
import com.kiyoshi.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(StockEvent.class);


    @Override
    public Order createOrder(Order order) {
        order.setId(null);

        // check the user exist
        User user = getUserById(order.getUserId());
        if(user == null) {
            throw new ResourceNotFoundException("User not existing", "id", order.getUserId());
        }

        // check the books exist and have stock enough for fulfill the order
        Boolean isStockValid = getIsStockAvailable(order.getOrder());
        if(!isStockValid) {
            throw new ResourceNotFoundException("Stock is not enough to fulfill the order", "ids", "");
        }

        // calculate the total

        // place order (save it to DB)
        Order newOrder = repository.save(order);

        // subtract quantity of the stock (kafka)
        StockEvent event = new StockEvent();
        event.setStatus("PENDING");
        event.setMessage("Subtracting quantity of books");
        event.setAction(Actions.SUBTRACT);
        event.setBooks(order.getOrder());

        // send notification (kafka)
        producer.sendMessage(event);
        LOGGER.info(String.format("Stock event send from order service => %s", event.toString()));

        // return order created
        return newOrder;
    }

    @Override
    public Order getOrder(String id) {
        Optional<Order> found = repository.findById(id);

        if(found.isEmpty()) {
            throw new ResourceNotFoundException("Order not found", "id", id);
        }

        return found.get();
    }

    @Override
    public List<Order> getAllOrders(String userId) {
        Optional<List<Order>> found = repository.findAllByUserId(userId);

        if(found.isEmpty()) {
            throw new ResourceNotFoundException("This user don't have orders placed", "userId", userId);
        }

        return found.get();
    }

    private User getUserById(String userId) {
        return restTemplate.getForObject(
                "http://USER-SERVICE/api/v1/user/{userId}",
                User.class,
                userId
        );
    }

    private Boolean getIsStockAvailable(List<Book> request) {
        return restTemplate.postForObject(
                "http://STOCK-SERVICE/api/v1/stock/isStockAvailable",
                request,
                Boolean.class
        );
    }
}
