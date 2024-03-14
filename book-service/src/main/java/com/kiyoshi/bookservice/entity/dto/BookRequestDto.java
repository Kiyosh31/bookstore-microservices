package com.kiyoshi.bookservice.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiyoshi.bookservice.entity.collection.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookRequestDto {
    @NotNull(message = "Missing book info")
    private Book book;

    @NotNull(message = "Missing quantity")
    private Integer availableQuantity;

    @NotNull(message = "Missing price")
    private Long price;
}
