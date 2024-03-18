package com.kiyoshi.authservice.entity.collection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.kiyoshi.authservice.entity.collection.Permission.*;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER(
            Set.of(
                    USER_CREATE,
                    USER_READ,
                    USER_UPDATE,
                    USER_DELETE,

                    BOOK_READ,

                    ORDER_CREATE,
                    ORDER_READ,

                    NOTIFICATION_READ
            )
    ),
    ADMIN(
            Set.of(
                    ADMIN_CREATE,
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,

                    BOOK_CREATE,
                    BOOK_READ,

                    ORDER_CREATE,
                    ORDER_READ,

                    NOTIFICATION_READ
            )
    ),
    GOD(
            Set.of(
                    USER_CREATE,
                    USER_READ,
                    USER_UPDATE,
                    USER_DELETE,

                    ADMIN_CREATE,
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,

                    BOOK_CREATE,
                    BOOK_READ,

                    NOTIFICATION_READ
            )
    );

    private final Set<Permission> permission;

//    public List<SimpleGrantedAuthority> getAuthorities() {
//        return getPermission()
//                .stream()
//                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
//                .collect(Collectors.toList());
////        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
////        return authorities;
//    }
}
