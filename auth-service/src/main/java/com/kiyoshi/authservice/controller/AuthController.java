package com.kiyoshi.authservice.controller;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;
import com.kiyoshi.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/generateToken")
    public ResponseEntity<TokenResponse> generateToken(@Valid @RequestBody TokenRequest request) {
        return new ResponseEntity<>(service.generateToken(request), HttpStatus.OK);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return new ResponseEntity<>(service.validateToken(authHeader), HttpStatus.OK);
    }
}
