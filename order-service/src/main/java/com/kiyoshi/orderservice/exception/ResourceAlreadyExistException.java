package com.kiyoshi.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.ALREADY_REPORTED)
public class ResourceAlreadyExistException extends RuntimeException {
    private String message;

    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
