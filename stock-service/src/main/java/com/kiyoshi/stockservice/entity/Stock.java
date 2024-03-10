package com.kiyoshi.stockservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stock")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {
    @Id
    private String id;

    @NotNull(message = "Missing bookId")
    private String bookId;

    @NotNull(message = "Missing bookName")
    private String bookName;

    @NotNull(message = "Missing availableQuantity")
    private Integer availableQuantity;

    @NotNull(message = "Missing price")
    private Long price;
}
