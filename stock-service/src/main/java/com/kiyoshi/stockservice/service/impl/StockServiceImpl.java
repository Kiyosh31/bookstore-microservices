package com.kiyoshi.stockservice.service.impl;

import com.kiyoshi.stockservice.entity.Stock;
import com.kiyoshi.stockservice.entity.StockRequest;
import com.kiyoshi.stockservice.repository.StockRepository;
import com.kiyoshi.stockservice.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class StockServiceImpl implements StockService {
    @Autowired
    private StockRepository repository;

    @Override
    public Boolean getIsStockAvailable(List<StockRequest> request) {
        // take out all the bookIds in an array
        List<String> bookIds = getListIdAndClean(request);

        // search for the bookIds in stock
        Optional<List<Stock>> bookArray = repository.findByBookIdIn(bookIds);

        if (bookArray.isEmpty()) {
            return false;
        }

        // check if all the order can be fulfilled
        if(bookArray.get().size() < bookIds.size()) {
            return false;
        }

        // check if there is enough quantity of books
        for(StockRequest item: request) {
            String bookId = item.getBookId();
            Integer quantity = item.getQuantity();

            for(Stock stock: bookArray.get()) {
                if(bookId.equals(stock.getBookId()) && quantity > stock.getAvailableQuantity()) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<String> getListIdAndClean(List<StockRequest> request) {
        // take out all the bookIds in an array
        List<String> bookIds = request.stream().map(StockRequest::getBookId).toList();

        // clean list of repeated elements turning into set
        Set<String> setWithoutDuplicates = new LinkedHashSet<>(bookIds);

        // return it to list
        return new ArrayList<>(setWithoutDuplicates);
    }
}
