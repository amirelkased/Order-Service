package com.fawry.orderservice.service;

import com.fawry.orderservice.common.ResponsePage;
import com.fawry.orderservice.common.ResponsePageMapper;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import com.fawry.orderservice.model.dto.NotificationRequest;
import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import com.fawry.orderservice.repository.OrderRepository;
import com.fawry.orderservice.service.thirdparty.NotificationService;
import com.fawry.orderservice.service.thirdparty.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    @Value("${order.merchant.id}")
    private Long merchantId;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final NotificationService notificationService;
    private final ResponsePageMapper pageMapper;
    private final OrderCoordinator orderCoordinator;

    @Transactional
    public Order createOrder(@NotNull CreateOrderRequest request) {
        List<OrderItem> orderItems = mapToOrderItems(request.getOrderItems());

        Order order = new Order();
        order.setMerchant(merchantId);
        order.setCustomerId(request.getCustomerId());
        order.setCouponCode(request.getCouponCode());
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        order.setOrderStatus(OrderStatus.PENDING);

        log.info("This is initial order -> {}", order);
        order = orderCoordinator.processOrder(order);

        Order savedOrder =  orderRepository.save(order);
        sendOrderNotification(savedOrder);
        return savedOrder;
    }

    private void sendOrderNotification(Order savedOrder) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .order(savedOrder)
                .build();
        notificationService.sendOrderNotification(notificationRequest);
    }

    private List<OrderItem> mapToOrderItems(List<OrderItemRequest> orderItemRequests) {
        List<ProductDto> products = productService.getProductsBySkus(orderItemRequests);
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

    private double calculateTotalAmount(@NotNull List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
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
}

