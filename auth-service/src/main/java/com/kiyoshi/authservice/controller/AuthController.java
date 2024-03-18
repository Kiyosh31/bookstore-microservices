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
    public ResponseEntity<TokenResponse> loginUser(@Valid @RequestBody TokenRequest request) {
        return new ResponseEntity<>(service.loginUser(request), HttpStatus.OK);
    }

    @GetMapping("/validateToken")
    public ResponseEntity<TokenResponse> validateToken(@RequestHeader HttpHeaders headers) {
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        assert authorizationHeader != null;
        return new ResponseEntity<>(service.validateToken(authorizationHeader.substring(7)), HttpStatus.OK);
    }
}
