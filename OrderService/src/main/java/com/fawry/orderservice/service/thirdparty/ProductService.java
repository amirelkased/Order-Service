package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.exception.ProductServiceException;
import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import com.fawry.orderservice.model.dto.ProductsWithPriceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String BASE_URL = "http://localhost:8080/api/v1/products/skus";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductDto> getProductsBySkus(@NotNull List<OrderItemRequest> orderItemList) {
        List<String> skus = orderItemList.stream().map(OrderItemRequest::getProductSku).toList();
        try {
            ResponseEntity<ProductsWithPriceResponse> responseEntity = restTemplate.postForEntity(
                    BASE_URL,
                    skus,
                    ProductsWithPriceResponse.class
            );
            ProductsWithPriceResponse products = responseEntity.getBody();
            if (products != null && !products.getProductDtos().isEmpty()) {
                return products.getProductDtos();
            } else {
                throw new ProductServiceException("No products found for the given skus");
            }
        } catch (HttpClientErrorException ex) {
            ProductsWithPriceResponse response = ex.getResponseBodyAs(ProductsWithPriceResponse.class);
            if (response != null){
                log.error("Client error: {}", response.getMessage());
            }
            throw new ProductServiceException("Error while retrieving products by skus %n %s".formatted(response.getMessage()));
        }
    }
}


