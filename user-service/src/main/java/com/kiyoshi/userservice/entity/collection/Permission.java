package com.kiyoshi.userservice.entity.collection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    ADMIN_CREATE("admin:create"),
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),

    BOOK_CREATE("book:create"),
    BOOK_READ("book:read"),

    ORDER_CREATE("order:create"),
    ORDER_READ("order:read"),

    NOTIFICATION_READ("notification:read");

    private final String permission;
}
