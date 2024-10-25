package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String BASE_URL = "http://localhost:8080/api/v1/products/skus";
    private final RestTemplate restTemplate = new RestTemplate();


    public List<ProductDto> getProductsBySkus(List<OrderItemRequest> orderItemList) {
        List<String> skus = orderItemList.stream().map(OrderItemRequest::getProductSku).toList();
        ResponseEntity<ProductDto[]> responseEntity = restTemplate.postForEntity(
                BASE_URL,
                skus,
                ProductDto[].class
        );
         ProductDto[] productDtos = responseEntity.getBody();
        return productDtos != null ? Arrays.asList(productDtos) : Collections.emptyList();
    }
}
