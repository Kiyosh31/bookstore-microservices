package com.kiyoshi.stockservice.service.impl;

import com.kiyoshi.basedomains.entity.Book;
import com.kiyoshi.stockservice.entity.Stock;
import com.kiyoshi.stockservice.exception.ResourceNotFoundException;
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
    public List<Stock> stockAvailable(List<Book> request) {
        // Response declaration
        List<Stock> response = new ArrayList<>();

        // take out all the bookIds in an array
        List<String> bookIds = getListIdAndClean(request);

        // search for the bookIds in stock
        Optional<List<Stock>> bookArray = repository.findByBookIdIn(bookIds);
        if (bookArray.isEmpty()) {
            throw new ResourceNotFoundException("No Stock for those books", "id", "");
        }

        // iterate to found the stock that can be bought
        for(Book item: request) {
            String bookId = item.getBookId();
            Integer quantity = item.getQuantity();

            // Populate the response
            for(Stock stock: bookArray.get()) {
                if(bookId.equals(stock.getBookId()) && quantity <= stock.getAvailableQuantity()) {
                    response.add(stock);
                }
            }
        }

        return response;
    }

    private List<String> getListIdAndClean(List<Book> request) {
        // take out all the bookIds in an array
        List<String> bookIds = request.stream().map(Book::getBookId).toList();

        // clean list of repeated elements turning into set
        Set<String> setWithoutDuplicates = new LinkedHashSet<>(bookIds);

        // return it to list
        return new ArrayList<>(setWithoutDuplicates);
    }
}
