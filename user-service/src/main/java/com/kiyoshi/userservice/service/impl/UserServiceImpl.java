package com.kiyoshi.userservice.service.impl;

import com.kiyoshi.commonutils.entity.Actions;
import com.kiyoshi.commonutils.entity.notification.Notification;
import com.kiyoshi.commonutils.entity.notification.NotificationEvent;
import com.kiyoshi.userservice.entity.collection.Permission;
import com.kiyoshi.userservice.entity.collection.Role;
import com.kiyoshi.userservice.entity.collection.User;
import com.kiyoshi.userservice.entity.dto.TokenRequest;
import com.kiyoshi.userservice.entity.dto.TokenResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;
import com.kiyoshi.userservice.exception.ResourceAlreadyExistException;
import com.kiyoshi.userservice.exception.ResourceNotFoundException;
import com.kiyoshi.userservice.kafka.NotificationProducer;
import com.kiyoshi.userservice.repository.UserRepository;
import com.kiyoshi.userservice.service.JwtService;
import com.kiyoshi.userservice.service.UserService;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.kiyoshi.userservice.entity.collection.Permission.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationProducer producer;

    @Autowired
    private JwtService jwtService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEvent.class);

    @Override
    public TokenResponse loginUser(TokenRequest request) {
        // check if user exists
        Optional<User> found = repository.findUserByEmail(request.getEmail());
        if(found.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", request.getEmail());
        }

        // validate password match
        if(!passwordEncoder.matches(request.getPassword(), found.get().getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtService.generateToken(found.get().getName());

        return TokenResponse.builder()
                .message("User authenticated successfully")
                .accessToken(token)
                .build();
    }

    @Override
    public TokenResponse validateToken(String token) {
        String email = jwtService.extractUsername(token);

        // check if user exists
        Optional<User> found = repository.findUserByEmail(email);
        if(found.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        // user is not deleted
        if(!found.get().getIsActive()) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        TokenResponse response = new TokenResponse();
        response.setMessage("Authentication failed");
        response.setAccessToken(null);

        if(jwtService.validateToken(token, found.get())) {
            response.setMessage("Authenticated successfully");
            response.setAccessToken(token);
        }

        return response;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Optional<User> founded = repository.findUserByEmail(userDto.getEmail());
        if(founded.isPresent()){
            throw new ResourceAlreadyExistException("User already exists");
        }

        userDto.setId(null);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        userDto.setPermissions(getPermissions(userDto.getRole().toUpperCase()));

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
        userDto.setPermissions(getPermissions(userDto.getRole()));
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
    public UserDto deleteUser(String id) {
        // validates user exist
        User user = validateUser(id);

        user.setIsActive(false);
        repository.save(user);

        return mapUserToDto(user);
    }

    @Override
    public UserDto reactivateUser(String id) {
        Optional<User> found = repository.findById(id);
        if(found.isEmpty()){
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
            if(!user.getIsActive()) {
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
                .permissions(userDto.getPermissions())
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
                .permissions(user.getPermissions())
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

    private Set<Permission> getPermissions(String role) {
        Set<Permission> permissions = new LinkedHashSet<>();

        if(Objects.equals(role, "")) {
            role = "USER";
        }

        switch (role) {
            case "USER":
                permissions = Set.of(
                        USER_CREATE,
                        USER_READ,
                        USER_UPDATE,
                        USER_DELETE,
                        BOOK_READ,
                        ORDER_CREATE,
                        ORDER_READ,
                        NOTIFICATION_READ
                );
                break;

            case "ADMIN":
                permissions = Set.of(
                        ADMIN_CREATE,
                        ADMIN_READ,
                        ADMIN_UPDATE,
                        ADMIN_DELETE,

                        BOOK_CREATE,
                        BOOK_READ,

                        ORDER_CREATE,
                        ORDER_READ,

                        NOTIFICATION_READ
                );
                break;

            case "GOD":
                permissions = Set.of(
                        USER_CREATE,
                        USER_READ,
                        USER_UPDATE,
                        USER_DELETE,

                        ADMIN_CREATE,
                        ADMIN_READ,
                        ADMIN_UPDATE,
                        ADMIN_DELETE,

                        BOOK_CREATE,
                        BOOK_READ,

                        ORDER_CREATE,
                        ORDER_READ,

                        NOTIFICATION_READ
                );
                break;

            default:
        }

        return permissions;
    }
}
