package com.fawry.orderservice.service.implementation;

import com.fawry.orderservice.common.ResponsePage;
import com.fawry.orderservice.common.ResponsePageMapper;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import com.fawry.orderservice.model.dto.OrderItemRequest;
import com.fawry.orderservice.model.dto.ProductDto;
import com.fawry.orderservice.repository.OrderRepository;
import com.fawry.orderservice.service.OrderCoordinatorService;
import com.fawry.orderservice.service.OrderService;
import com.fawry.orderservice.thirdparty.NotificationService;
import com.fawry.orderservice.thirdparty.ProductService;
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
import java.util.UUID;

import static com.fawry.orderservice.model.mapper.OrderItemsMapper.mapToOrderItems;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Value("${order.merchant.id}")
    private Long merchantId;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final ProductService productService;
    private final ResponsePageMapper pageMapper;
    private final OrderCoordinatorService orderCoordinatorService;

    @Transactional
    public Order createOrder(@NotNull CreateOrderRequest request) {
        log.info("Order products: {}", request.getOrderItemRequestList());
        Order order = prepareOrderFromRequest(request);

        log.info("This is initial order -> {}", order);
        orderCoordinatorService.processOrder(order);

        Order savedOrder = orderRepository.save(order);

        notificationService.sendOrderNotification(savedOrder);
        return savedOrder;
    }

    public ResponsePage<Order> getAllOrdersByCustomerIdAndRangeDate(Pageable pageable, Long customerId, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            Page<Order> result = orderRepository.findOrdersByCustomerId(pageable, customerId);
            return pageMapper.toResponsePage(result);
        }
        LocalDateTime startDateTime = (from != null) ? from.atStartOfDay() : LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.MIN);
        LocalDateTime endDateTime = (to != null) ? to.atTime(LocalTime.MAX) : LocalDateTime.now();
        Page<Order> result = orderRepository.findAllOrdersBetweenRangeDates(pageable, customerId, startDateTime, endDateTime);
        return pageMapper.toResponsePage(result);
    }

    public Order getOrderById(Long customerId, Long orderId) {
        return orderRepository.findOrderByIdAndCustomerId(orderId, customerId).orElseThrow(
                () -> new EntityNotFoundException("No Order by Id %d for customer %d".formatted(orderId, customerId))
        );
    }

    @NotNull
    public Order prepareOrderFromRequest(@NotNull CreateOrderRequest request) {
        List<ProductDto> productsDtos = getOrderProductsWithPrices(request.getOrderItemRequestList());
        List<OrderItem> orderItems = mapToOrderItems(request.getOrderItemRequestList(), productsDtos);

        Order order = new Order();
        order.setTransactionId(UUID.randomUUID().toString());
        order.setMerchant(merchantId);
        order.setCustomerId(request.getCustomerId());
        order.setCouponCode(request.getCouponCode());
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        order.setOrderStatus(OrderStatus.NONE);

        return order;
    }

    private List<ProductDto> getOrderProductsWithPrices(List<OrderItemRequest> orderItems) {
        return productService.getProductsBySkus(orderItems);
    }

    private double calculateTotalAmount(@NotNull List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}

