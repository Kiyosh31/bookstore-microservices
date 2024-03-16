package com.kiyoshi.commonutils.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class Event {
    private String message;
    private String status;
    private Actions action;
}
