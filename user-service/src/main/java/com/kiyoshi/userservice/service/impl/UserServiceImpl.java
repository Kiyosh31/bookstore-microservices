package com.kiyoshi.userservice.service.impl;

import com.kiyoshi.basedomains.entity.Notification;
import com.kiyoshi.basedomains.entity.NotificationEvent;
import com.kiyoshi.userservice.entity.collection.Role;
import com.kiyoshi.userservice.entity.collection.User;
import com.kiyoshi.userservice.entity.dto.DeleteResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;
import com.kiyoshi.userservice.exception.ResourceAlreadyExistException;
import com.kiyoshi.userservice.exception.ResourceNotFoundException;
import com.kiyoshi.userservice.kafka.NotificationProducer;
import com.kiyoshi.userservice.repository.UserRepository;
import com.kiyoshi.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEvent.class);


    @Override
    public UserDto createUser(UserDto userDto) {
        Optional<User> founded = repository.findUserByEmail(userDto.getEmail());
        if(founded.isPresent()){
            throw new ResourceAlreadyExistException("User already exists");
        }

        userDto.setId(null);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        userDto.setRole(Role.USER);

        User newUser = mapDtoToUser(userDto, true);
        repository.save(newUser);
        return mapUserToDto(newUser);
    }

    @Override
    public UserDto getUser(String id) {
        // validates user exist
        Optional<User> existingUser = repository.findActiveById(id);
        if(existingUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found", "id", id);
        }

        return mapUserToDto(existingUser.get());
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        // validates user exist
        Optional<User> existingUser = repository.findActiveById(id);
        if(existingUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found", "id", id);
        }

        // update user
        userDto.setId(existingUser.get().getId());
        userDto.setRole(Role.USER);
        User updatedUser = mapDtoToUser(userDto, true);

        //save it to db
        repository.save(updatedUser);

        // create event
        NotificationEvent event = createNotificationEvent(updatedUser.getId());

        // send notification event (kafka)
        producer.sendMessage(event);
        LOGGER.info(String.format("Notification event send from user service => %s", event.toString()));

        // return modified user
        return mapUserToDto(updatedUser);
    }

    @Override
    public DeleteResponse deleteUser(String id) {
        // validates user exist
        Optional<User> existingUser = repository.findActiveById(id);
        if(existingUser.isEmpty()) {
            throw new ResourceNotFoundException("User not found", "id", id);
        }

        User deletedUser = existingUser.get();
        deletedUser.setIsActive(false);
        repository.save(deletedUser);

        return DeleteResponse.builder()
                .message("User deleted successfully")
                .status(200)
                .build();
    }


    private User mapDtoToUser(UserDto userDto, Boolean isActive) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .card(userDto.getCard())
                .isActive(isActive)
                .build();
    }

    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .card(user.getCard())
                .build();
    }

    private NotificationEvent createNotificationEvent(String userId) {
        return NotificationEvent.builder()
                .status("PENDING")
                .message("Creating notification in DB")
                .notification(createNotification(userId))
                .build();
    }

    private Notification createNotification(String userId) {
        return Notification.builder()
                .userId(userId)
                .title("User updated")
                .description("User updated successfully")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
