package com.kiyoshi.orderservice.service.impl;

import com.kiyoshi.basedomains.entity.Actions;
import com.kiyoshi.basedomains.entity.Book;
import com.kiyoshi.basedomains.entity.Stock;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.orderservice.entity.Order;
import com.kiyoshi.orderservice.entity.User;
import com.kiyoshi.orderservice.exception.ResourceNotFoundException;
import com.kiyoshi.orderservice.kafka.StockProducer;
import com.kiyoshi.orderservice.repository.OrderRepository;
import com.kiyoshi.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        List<Stock> validStock = getStockAvailable(order.getBooks());
        if(validStock.isEmpty()) {
            throw new ResourceNotFoundException("Stock is not enough to fulfill the order", "ids", "");
        }
        log.info("jajaja: {}", validStock);

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

        // place order (save it to DB)
        Order newOrder = repository.save(order);

        // send event to subtract quantity in stock (kafka)
        StockEvent event = createStockEvent(order);
        producer.sendMessage(event);
        LOGGER.info(String.format("Stock event send from order service => %s", event.toString()));

        // return order created
        return newOrder;
    }

    private static StockEvent createStockEvent(Order order) {
        StockEvent event = new StockEvent();
        event.setStatus("PENDING");
        event.setMessage("Subtracting quantity of books");
        event.setAction(Actions.SUBTRACT);

        List<Book> booksToSubtract = new ArrayList<>();
        for(Book item: order.getBooks()) {
            Book newBook = new Book();
            newBook.setBookId(item.getBookId());
            newBook.setQuantity(item.getQuantity());
            booksToSubtract.add(newBook);
        }

        event.setBooks(booksToSubtract);
        return event;
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
}
