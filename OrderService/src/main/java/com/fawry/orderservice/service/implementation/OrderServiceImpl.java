package com.fawry.orderservice.service.implementation;

import com.fawry.orderservice.common.ResponsePage;
import com.fawry.orderservice.common.ResponsePageMapper;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import com.fawry.orderservice.repository.OrderRepository;
import com.fawry.orderservice.service.OrderCoordinatorService;
import com.fawry.orderservice.service.OrderService;
import com.fawry.orderservice.thirdparty.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final ResponsePageMapper pageMapper;
    private final OrderCoordinatorService orderCoordinatorService;

    @Transactional
    public Order createOrder(@NotNull CreateOrderRequest request) {
        log.info("Order products: {}", request.getOrderItemRequestList());
        Order order = orderCoordinatorService.prepareOrderFromRequest(request);

        log.info("This is initial order -> {}", order);
        order = orderCoordinatorService.processOrder(order);

        Order savedOrder = orderRepository.save(order);

        notificationService.sendOrderNotification(savedOrder);
        return savedOrder;
    }

    public ResponsePage<Order> getAllOrdersByCustomerIdAndRangeDate(Pageable pageable, Long customerId, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            Page<Order> result = orderRepository.findOrdersByCustomerId(pageable, customerId);
            return pageMapper.toResponsePage(result);
        }
        LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime endDateTime = (to != null) ? to.atTime(LocalTime.MAX) : LocalDateTime.MAX;
        Page<Order> result = orderRepository.findAllOrdersBetweenRangeDates(pageable, customerId, startDateTime, endDateTime);
        return pageMapper.toResponsePage(result);
    }

    public Order getOrderById(Long customerId, Long orderId) {
        return orderRepository.findOrderByIdAndCustomerId(orderId, customerId).orElseThrow(
                () -> new EntityNotFoundException("No Order by Id %d for customer %d".formatted(orderId, customerId))
        );
    }
}

