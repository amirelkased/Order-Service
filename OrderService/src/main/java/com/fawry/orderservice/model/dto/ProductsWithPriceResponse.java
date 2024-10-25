package com.fawry.orderservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsWithPriceResponse {
    private String status;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String message;
    private List<ProductDto> productDtos;
}
