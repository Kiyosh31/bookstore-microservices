package com.kiyoshi.userservice.service.impl;

import com.kiyoshi.basedomains.exception.ResourceAlreadyExistException;
import com.kiyoshi.basedomains.exception.ResourceNotFoundException;
import com.kiyoshi.userservice.entity.User;
import com.kiyoshi.userservice.repository.UserRepository;
import com.kiyoshi.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;

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
        User founded = findUserInDb(id);

        founded.setName(user.getName());
        founded.setEmail(user.getEmail());
        founded.setPassword(user.getPassword());
        founded.setCard(user.getCard());
        founded.setRole(user.getRole());

        repository.save(founded);

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
