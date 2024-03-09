package com.kiyoshi.bookservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "book")
public class Book {
    @Id
    private String id;

    @NotNull(message = "Missing name")
    private String name;

    @NotNull(message = "Missing editorial")
    private String editorial;

    @NotNull(message = "Missing pages")
    private int pages;

    @NotNull(message = "Missing author")
    private String author;

    @NotNull(message = "Missing price")
    private long price;
}
