package com.kiyoshi.bookservice.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
    private String id;

    @NotNull(message = "Missing name")
    private String name;

    @NotNull(message = "Missing editorial")
    private String editorial;

    @NotNull(message = "Missing pages")
    private int pages;

    @NotNull(message = "Missing author")
    private String author;

    @NotNull(message = "Missing cover image")
    private CoverImage coverImage;
}
