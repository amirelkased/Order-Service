package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductDto> getProductsBySkus(List<OrderItemRequest> orderItemList);
}
