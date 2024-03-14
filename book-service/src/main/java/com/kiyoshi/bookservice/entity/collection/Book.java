package com.kiyoshi.bookservice.entity.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "book")
public class Book {
    @Id
    private String id;

    private String name;

    private String editorial;

    private int pages;

    private String author;
}
