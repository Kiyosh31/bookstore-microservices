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
import com.kiyoshi.userservice.repository.AdminRepository;
import com.kiyoshi.userservice.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationProducer producer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEvent.class);

    @Override
    public UserDto createAdmin(UserDto userDto) {
        Optional<User> founded = repository.findAdminByEmail(userDto.getEmail());
        if(founded.isPresent()){
            throw new ResourceAlreadyExistException("Admin already exists");
        }

        userDto.setId(null);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        userDto.setRole(Role.ADMIN);

        User newUser = mapDtoToAdmin(userDto, true);
        repository.save(newUser);
        return mapAdminToDto(newUser);
    }

    @Override
    public UserDto getAdmin(String id) {
        User admin = validateAdmin(id);
        return mapAdminToDto(admin);
    }

    @Override
    public UserDto updateAdmin(String id, UserDto userDto) {
        // validates admin exist
        User admin = validateAdmin(id);

        // update admin
        userDto.setId(admin.getId());
        userDto.setRole(Role.ADMIN);
        User updatedAdmin = mapDtoToAdmin(userDto, true);

        //save it to db
        repository.save(updatedAdmin);

        // create event
        NotificationEvent event = createNotificationEvent(updatedAdmin.getId());

        // send notification event (kafka)
        producer.sendMessage(event);
        LOGGER.info(String.format("Notification event send from user service => %s", event.toString()));

        // return modified admin
        return mapAdminToDto(updatedAdmin);
    }

    @Override
    public DeleteResponse deleteAdmin(String id) {
        // validates admin exist
        User admin = validateAdmin(id);

        admin.setIsActive(false);
        repository.save(admin);

        return DeleteResponse.builder()
                .message("Admin deleted successfully")
                .status(200)
                .build();
    }

    @Override
    public UserDto reactivateAdmin(String id) {
        Optional<User> found = repository.findById(id);
        if(found.isEmpty()){
            throw new ResourceNotFoundException("Admin", "id", id);
        }

        if(!found.get().getRole().name().equals(Role.ADMIN.name())) {
            throw new ResourceNotFoundException("Admin", "id", id);
        }

        User reactivatedAdmin = found.get();
        reactivatedAdmin.setIsActive(true);
        repository.save(reactivatedAdmin);

        return mapAdminToDto(reactivatedAdmin);
    }

    private User validateAdmin(String id) {
        // validates admin exist
        Optional<User> found = repository.findById(id);
        if(found.isPresent()) {
            User admin = found.get();

            // admin is active
            // admin role: ADMIN
            if(!admin.getIsActive() || !admin.getRole().name().equals(Role.ADMIN.name())) {
                throw new ResourceNotFoundException("Admin", "id", id);
            }

            return admin;
        }

        throw new ResourceNotFoundException("Admin", "id", id);
    }

    private User mapDtoToAdmin(UserDto userDto, Boolean isActive) {
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

    private UserDto mapAdminToDto(User user) {
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
                .title("Admin updated")
                .description("Admin updated successfully")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
