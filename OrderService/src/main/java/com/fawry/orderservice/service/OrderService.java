package com.fawry.orderservice.service;

import com.fawry.orderservice.common.ResponsePage;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface OrderService {
    Order createOrder(@NotNull CreateOrderRequest request);

    ResponsePage<Order> getAllOrdersByCustomerIdAndRangeDate(Pageable pageable, Long customerId, LocalDate from, LocalDate to);

    Order getOrderById(Long customerId, Long orderId) throws EntityNotFoundException;
}
