package com.kiyoshi.orderservice.service.impl;

import com.kiyoshi.commonutils.*;
import com.kiyoshi.orderservice.entity.Order;
import com.kiyoshi.orderservice.entity.User;
import com.kiyoshi.orderservice.exception.ResourceNotFoundException;
import com.kiyoshi.orderservice.kafka.NotificationProducer;
import com.kiyoshi.orderservice.kafka.StockProducer;
import com.kiyoshi.orderservice.repository.OrderRepository;
import com.kiyoshi.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockProducer stockProducer;

    @Autowired
    private NotificationProducer notificationProducer;

    private static final Logger STOCK_LOGGER = LoggerFactory.getLogger(StockEvent.class);

    private static final Logger NOTIFICATION_LOGGER = LoggerFactory.getLogger(NotificationEvent.class);


    @Override
    public Order createOrder(Order order) {
        order.setId(null);

        // check the user exist
        User user = getUserById(order.getUserId());
        if(user == null) {
            throw new ResourceNotFoundException("User not existing", "id", order.getUserId());
        }

        // check the books exist and have stock enough for fulfill the order
        List<Stock> validStock = getStockAvailable(order.getBooks());
        if(validStock.isEmpty()) {
            throw new ResourceNotFoundException("Stock is not enough to fulfill the order", "ids", "");
        }

        // delete request invalid ids (doesn't exist on stock)
        List<Book> cleanedBookIds = new ArrayList<>();
        for(Book book: order.getBooks()) {
            for(Stock item: validStock) {
                if(book.getBookId().equals(item.getBookId())){
                    cleanedBookIds.add(book);
                }
            }
        }
        order.setBooks(cleanedBookIds);

        // calculate the total
        long total = 0L;
        for(Book book: order.getBooks()) {
            for(Stock item: validStock) {
                if(book.getBookId().equals(item.getBookId())) {
                    total += item.getPrice() * book.getQuantity();
                }
            }
        }
        order.setTotal(total);
        order.setCreatedAt(LocalDateTime.now());

        // place order (save it to DB)
        Order newOrder = repository.save(order);

        // send event to subtract quantity in stock (kafka)
        StockEvent stockEvent = createStockEvent(order);
        stockProducer.sendMessage(stockEvent);
        STOCK_LOGGER.info(String.format("Stock event send from order service => %s", stockEvent.toString()));

        // send event to notification service (kafka)
        NotificationEvent notificationEvent = createNotificationEvent(order.getUserId());
        notificationProducer.sendMessage(notificationEvent);
        NOTIFICATION_LOGGER.info(String.format("Notification event send from order service => %s", notificationEvent.toString()));

        // return order created
        return newOrder;
    }

    private static StockEvent createStockEvent(Order order) {
        List<Book> booksToSubtract = new ArrayList<>();
        for(Book item: order.getBooks()) {
            booksToSubtract.add(new Book(item.getBookId(), item.getQuantity()));
        }

        return StockEvent.builder()
                .status("PENDING")
                .message("Subtracting quantity of books")
                .action(Actions.SUBTRACT)
                .books(booksToSubtract)
                .build();
    }

    @Override
    public Order getOrder(String userId, String orderId) {
        // check the user exists
        User user = getUserById(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User does not exist", "id", userId);
        }

        // Check the order exists
        Optional<Order> found = repository.findById(orderId);
        if(found.isEmpty()) {
            throw new ResourceNotFoundException("Order not found", "id", orderId);
        }

        return found.get();
    }

    @Override
    public List<Order> getAllOrders(String userId) {
        // check the user exists
        User user = getUserById(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User does not exist", "id", userId);
        }

        // check the orders exist
        Optional<List<Order>> found = repository.findByUserIdIn(userId);
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

    private List<Stock> getStockAvailable(List<Book> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Book>> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<List<Stock>> responseEntity = restTemplate.exchange(
                "http://STOCK-SERVICE/api/v1/stock/stockAvailable",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<Stock>>() {}
        );

        return responseEntity.getBody();
    }

    private static NotificationEvent createNotificationEvent(String userId) {
        return NotificationEvent.builder()
                .status("PENDING")
                .message("Creating new Order")
                .notification(createNotification(userId))
                .build();
    }

    private static Notification createNotification(String userId) {
        return Notification.builder()
                .title("Order placed successfully")
                .description("Order was placed successfully with all info")
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .build();
    }
}
