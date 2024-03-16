package com.kiyoshi.commonutils.entity.notification;

import com.kiyoshi.commonutils.entity.Actions;
import com.kiyoshi.commonutils.entity.Event;
import com.kiyoshi.commonutils.entity.Stock.Book;
import com.kiyoshi.commonutils.entity.Stock.Stock;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class NotificationEvent extends Event {
    private Notification notification;
}
