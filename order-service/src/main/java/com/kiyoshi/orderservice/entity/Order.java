package com.kiyoshi.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiyoshi.commonutils.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "order")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @Id
    private String id;

    @NotNull(message = "Missing userId")
    private String userId;

    @NotNull(message = "Missing books")
    private List<Book> books;

    private Long total;

    private LocalDateTime createdAt;
}
