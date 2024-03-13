package com.kiyoshi.userservice.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteResponse {
    private String message;
    private Integer status;
}
