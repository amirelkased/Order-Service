package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.OutOfStockException;
import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.dto.StockRequest;
import com.fawry.orderservice.thirdparty.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.fawry.orderservice.model.mapper.OrderItemsMapper.mapToStockRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    @Value("${store.api.url}")
    private String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public void consumeStock(List<OrderItem> orderItems) {
        List<StockRequest> stockRequestList = mapToStockRequest(orderItems);
        try {
            restTemplate.postForEntity(BASE_URL.concat("/consume"), stockRequestList, Void.class);
            log.info("Stock consumed successfully for all items.");
        } catch (HttpClientErrorException e) {
            log.error("One or more products are out of stock. Details:");
            stockRequestList.forEach(item -> log.error("Product SKU: {}, Quantity: {}", item.getProductSku(), item.getQuantity()));
            throw new OutOfStockException("One or more products are out of stock.");
        }
    }

    @Override
    public void releaseStock(List<OrderItem> orderItems) {
        List<StockRequest> stockRequestList = mapToStockRequest(orderItems);
        try {
//            restTemplate.postForEntity(BASE_URL.concat("/release"), stockRequestList, Void.class);
            restTemplate.postForEntity("http://localhost:8080/stocks/unconsume", stockRequestList, Void.class);
        } catch (HttpClientErrorException e) {
            log.error("Unable to release Consumed products");
            stockRequestList.forEach(item -> log.error("Product SKU: {}, Quantity: {}", item.getProductSku(), item.getQuantity()));
            throw new OutOfStockException("One or more products are out of stock.");
        }
    }
}

