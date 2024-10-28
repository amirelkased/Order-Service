package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.ErrorResponse;
import com.fawry.orderservice.exception.ProductServiceException;
import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import com.fawry.orderservice.model.dto.ProductsWithPriceResponse;
import com.fawry.orderservice.thirdparty.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Value("${product.api.url}")
    private String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
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
            ErrorResponse errorResponse = ex.getResponseBodyAs(ErrorResponse.class);
            if (errorResponse != null) {
                log.error("Client error: {}", ex.getMessage());
            }
            throw new ProductServiceException("Error while retrieving products by skus : %s".formatted(errorResponse.getMessage()));
        }
    }
}


