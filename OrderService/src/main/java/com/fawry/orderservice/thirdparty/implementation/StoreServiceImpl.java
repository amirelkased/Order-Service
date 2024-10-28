package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.OutOfStockException;
import com.fawry.orderservice.model.dto.StockRequest;
import com.fawry.orderservice.thirdparty.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    @Value("${store.api.url}")
    private String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void consumeStock(List<StockRequest> stockRequestList) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, stockRequestList, Void.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Stock consumed successfully for all items.");
            } else {
                log.error("Failed to consume stock. Unexpected status code: {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("One or more products are out of stock. Details:");
            stockRequestList.forEach(item -> log.error("Product SKU: {}, Quantity: {}", item.getProductSku(), item.getQuantity()));
            throw new OutOfStockException("One or more products are out of stock.");
        }
    }
}

