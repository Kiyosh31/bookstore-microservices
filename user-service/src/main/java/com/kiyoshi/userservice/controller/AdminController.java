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
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AdminService service;

    @PostMapping("/create")
    public ResponseEntity<UserDto> createAdmin(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(service.createAdmin(userDto), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
//    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<UserDto> getAdmin(@PathVariable String id ) {
        return new ResponseEntity<>(service.getAdmin(id), HttpStatus.FOUND);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserDto> updateAdmin(@PathVariable String id, @Valid @RequestBody UserDto userDto) {

        return new ResponseEntity<>(service.updateAdmin(id, userDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeleteResponse> deleteAdmin(@PathVariable String id) {
        return new ResponseEntity<>(service.deleteAdmin(id), HttpStatus.OK);
    }

    @GetMapping("/reactivate/{id}")
    public ResponseEntity<UserDto> reactivateAdmin(@PathVariable String id) {
        return new ResponseEntity<>(service.reactivateAdmin(id), HttpStatus.OK);
    }
}
