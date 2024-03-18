package com.kiyoshi.bookservice.entity.dto;

import lombok.Data;

@Data
public class CoverImage {
    private String name;
    private byte[] file;
}
