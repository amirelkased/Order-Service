package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    public static final String BASE_URL = "http://localhost:8080/api/v1/store/stocks";
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean consumeStock(List<OrderItem> orderItems) {
        ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, orderItems, Void.class);
        return response.getStatusCode() == HttpStatus.OK;
    }
}

