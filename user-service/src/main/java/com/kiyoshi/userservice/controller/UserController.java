package com.kiyoshi.userservice.controller;

import com.kiyoshi.userservice.entity.dto.DeleteResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;
import com.kiyoshi.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
//@PreAuthorize("hasRole('USER')")
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(service.createUser(userDto), HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        return new ResponseEntity<>(service.getUser(id), HttpStatus.FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(service.updateUser(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeleteResponse> deleteUser(@PathVariable String id) {
        return new ResponseEntity<>(service.deleteUser(id), HttpStatus.OK);
    }

    @GetMapping("/reactivate/{id}")
    public ResponseEntity<UserDto> reactivateUser(@PathVariable String id) {
        return new ResponseEntity<>(service.reactivateUser(id), HttpStatus.OK);
    }
}
