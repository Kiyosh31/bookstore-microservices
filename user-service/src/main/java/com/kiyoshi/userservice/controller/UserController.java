package com.kiyoshi.userservice.controller;

import com.kiyoshi.userservice.entity.User;
import com.kiyoshi.userservice.entity.UserSigningRequest;
import com.kiyoshi.userservice.entity.UserSigningResponse;
import com.kiyoshi.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/signing")
    public ResponseEntity<String> signingUser(@Valid @RequestBody UserSigningRequest request) {
        return new ResponseEntity<>("hey!", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return new ResponseEntity<>(service.getUser(id), HttpStatus.FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @Valid @RequestBody User user) {
        return new ResponseEntity<>(service.updateUser(id, user), HttpStatus.OK);
    }
}
