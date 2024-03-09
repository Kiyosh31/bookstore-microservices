package com.kiyoshi.bookservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {
    @NotNull(message = "Missing book info")
    private Book book;

    @NotNull(message = "Missing quantity")
    private Integer quantity;
}
