package com.fawry.orderservice.service;

import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface OrderCoordinatorService {
    @NotNull
    Order prepareOrderFromRequest(@NotNull CreateOrderRequest request);

    Order processOrder(Order order);
}
