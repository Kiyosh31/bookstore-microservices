package com.kiyoshi.userservice.service.impl;

import com.kiyoshi.commonutils.entity.Actions;
import com.kiyoshi.commonutils.entity.notification.Notification;
import com.kiyoshi.commonutils.entity.notification.NotificationEvent;
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
        User user = validateUser(id);
        return mapUserToDto(user);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        // validates user exist
        User user = validateUser(id);

        // update user
        userDto.setId(user.getId());
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
        User user = validateUser(id);

        user.setIsActive(false);
        repository.save(user);

        return DeleteResponse.builder()
                .message("User deleted successfully")
                .status(200)
                .build();
    }

    @Override
    public UserDto reactivateUser(String id) {
        Optional<User> found = repository.findById(id);
        if(found.isEmpty()){
            throw new ResourceNotFoundException("User", "id", id);
        }

        if(!found.get().getRole().name().equals(Role.USER.name())) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        User reactivatedUser = found.get();
        reactivatedUser.setIsActive(true);
        repository.save(reactivatedUser);

        return mapUserToDto(reactivatedUser);
    }

    private User validateUser(String id) {
        // user db
        Optional<User> found = repository.findById(id);
        if(found.isPresent()) {
            User user = found.get();

            // user is active
            // user role: USER
            if(!user.getIsActive() || !user.getRole().name().equals(Role.USER.name())) {
                throw new ResourceNotFoundException("User", "id", id);
            }

            return user;
        }

        throw new ResourceNotFoundException("User", "id", id);
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
                .action(Actions.CREATE)
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
