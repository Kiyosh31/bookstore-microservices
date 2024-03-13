package com.kiyoshi.userservice.controller;

import com.kiyoshi.userservice.entity.collection.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @PostMapping("/create")
    public ResponseEntity<String> createAdmin() {
        return new ResponseEntity<>("hola", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getAdmin() {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateAdmin() {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteAdmin() {
        return null;
    }
}
