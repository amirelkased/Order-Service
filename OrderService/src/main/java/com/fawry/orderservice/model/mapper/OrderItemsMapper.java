package com.fawry.orderservice.model.mapper;

import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemsMapper {

    private OrderItemsMapper(){}

    public static List<OrderItem> mapToOrderItems(@NotNull List<OrderItemRequest> orderItemRequests, List<ProductDto> products) {
        // Map the order items and associate them with their prices
        return orderItemRequests.stream()
                .map(item -> {
                    ProductDto product = products.stream()
                            .filter(p -> p.getSku().equals(item.getProductSku()))
                            .findFirst()
                            .orElseThrow(
                                    () -> new EntityNotFoundException(
                                            "No Product With Sku %s".formatted(item.getProductSku())
                                    )
                            );

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductSku(item.getProductSku());
                    orderItem.setPrice(product.getPrice());
                    orderItem.setQuantity(item.getQuantity());
                    return orderItem;
                })
                .toList();
    }
}
