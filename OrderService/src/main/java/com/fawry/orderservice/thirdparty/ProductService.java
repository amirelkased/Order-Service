package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsBySkus(List<OrderItemRequest> orderItemList);
}
