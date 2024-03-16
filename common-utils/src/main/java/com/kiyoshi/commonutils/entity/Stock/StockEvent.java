package com.kiyoshi.commonutils.entity.Stock;

import com.kiyoshi.commonutils.entity.Actions;
import com.kiyoshi.commonutils.entity.Event;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class StockEvent extends Event {
    private Stock stock;
    private List<Book> books;

}