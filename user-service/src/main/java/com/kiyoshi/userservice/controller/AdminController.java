package com.kiyoshi.userservice.controller;

import com.kiyoshi.userservice.entity.dto.DeleteResponse;
import com.kiyoshi.userservice.entity.dto.UserDto;
import com.kiyoshi.userservice.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    @Autowired
    private AdminService service;

    @PostMapping("/create")
    public ResponseEntity<UserDto> createAdmin(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(service.createAdmin(userDto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getAdmin(@PathVariable String id ) {
        return new ResponseEntity<>(service.getAdmin(id), HttpStatus.FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateAdmin(@PathVariable String id, @Valid @RequestBody UserDto userDto) {

        return new ResponseEntity<>(service.updateAdmin(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteAdmin(@PathVariable String id) {
        return new ResponseEntity<>(service.deleteAdmin(id), HttpStatus.OK);
    }
}
