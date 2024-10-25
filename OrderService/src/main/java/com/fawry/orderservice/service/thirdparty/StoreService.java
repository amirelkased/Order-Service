package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.exception.OutOfStockException;
import com.fawry.orderservice.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private static final String BASE_URL = "http://localhost:8081/api/v1/store/stocks";
    private final RestTemplate restTemplate = new RestTemplate();

    public void consumeStock(List<OrderItem> orderItems) {
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, orderItems, Void.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Stock consumed successfully for all items.");
            } else {
                log.error("Failed to consume stock. Unexpected status code: {}", response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("One or more products are out of stock. Details:");
            orderItems.forEach(item -> log.error("Product SKU: {}, Quantity: {}", item.getProductSku(), item.getQuantity()));
            throw new OutOfStockException("One or more products are out of stock.");
        }
    }
}

