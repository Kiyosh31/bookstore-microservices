package com.kiyoshi.userservice.service.impl;

import com.kiyoshi.basedomains.entity.Notification;
import com.kiyoshi.basedomains.entity.NotificationEvent;
import com.kiyoshi.basedomains.entity.StockEvent;
import com.kiyoshi.userservice.entity.User;
import com.kiyoshi.userservice.exception.ResourceAlreadyExistException;
import com.kiyoshi.userservice.exception.ResourceNotFoundException;
import com.kiyoshi.userservice.kafka.NotificationProducer;
import com.kiyoshi.userservice.repository.UserRepository;
import com.kiyoshi.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private NotificationProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEvent.class);


    @Override
    public User createUser(User user) {
        Optional<User> founded = repository.findUserByEmail(user.getEmail());

        if(founded.isPresent()){
            throw new ResourceAlreadyExistException("User already exists");
        }

        user.setId(null);
        return repository.save(user);
    }

    @Override
    public User getUser(String id) {
        return findUserInDb(id);
    }

    @Override
    public User updateUser(String id, User user) {
        // validates user exist
        User founded = findUserInDb(id);

        // update user and save it to DB
        founded.setName(user.getName());
        founded.setEmail(user.getEmail());
        founded.setPassword(user.getPassword());
        founded.setCard(user.getCard());
        founded.setRole(user.getRole());
        repository.save(founded);

        // create event
        NotificationEvent event = new NotificationEvent();
        event.setStatus("PENDING");
        event.setMessage("Creating notification in database");

        Notification notification = new Notification();
        notification.setUserId(founded.getId());
        notification.setTitle("User updated");
        notification.setDescription("User updated successfully!");
        notification.setCreatedAt(LocalDateTime.now());
        event.setNotification(notification);

        // send notification event (kafka)
        producer.sendMessage(event);
        LOGGER.info(String.format("Notification event send from user service => %s", event.toString()));

        // return modified user
        return founded;
    }

    private User findUserInDb(String id) {
        Optional<User> found = repository.findById(id);

        if(found.isEmpty()) {
            throw new ResourceNotFoundException("User not found", "id", id);
        }

        return found.get();
    }
}
