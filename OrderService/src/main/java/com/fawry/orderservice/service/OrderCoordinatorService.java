package com.fawry.orderservice.service;


import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.PaymentStatus;
import com.fawry.orderservice.model.dto.*;
import com.fawry.orderservice.repository.OrderRepository;
import com.fawry.orderservice.service.thirdparty.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fawry.orderservice.model.mapper.OrderItemsMapper.mapToOrderItems;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCoordinatorService {
    @Value("${order.merchant.id}")
    private Long merchantId;
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;
    private final ProductService productService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;


    @Transactional
    public Order createOrder(@NotNull CreateOrderRequest request) {
        log.info("Order products: {}", request.getOrderItemRequestList());
        Order order = prepareOrderFromRequest(request);

        log.info("This is initial order -> {}", order);
        order = processOrder(order);

        Order savedOrder =  orderRepository.save(order);

        notificationService.sendOrderNotification(savedOrder);
        return savedOrder;
    }

    private @NotNull Order prepareOrderFromRequest(@NotNull CreateOrderRequest request) {
        List<ProductDto> productsDtos = getOrderProductsWithPrices(request.getOrderItemRequestList());
        List<OrderItem> orderItems = mapToOrderItems(request.getOrderItemRequestList(), productsDtos);

        Order order = new Order();
        order.setMerchant(merchantId);
        order.setCustomerId(request.getCustomerId());
        order.setCouponCode(request.getCouponCode());
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        order.setOrderStatus(OrderStatus.PENDING);

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


    public Order processOrder(Order order) {
        consumeCoupon(order);
        log.info("Consume Coupon Success");

        consumeStock(order);
        log.info("Consume Stock Success");

        withdrawAmountFromCustomer(order);
        log.info("Withdraw Success");

        depositAmountToMerchant(order);
        log.info("Deposite Success");

        order.setOrderStatus( OrderStatus.COMPLETED);
        return order;
    }

    private void depositAmountToMerchant(@NotNull Order order) {
        TransactionResponse merchantTransactionResponse = bankService.deposit(order.getMerchant(), getTransactionValue(order));
        order.setMerchantTransactionId(merchantTransactionResponse.getTransactionId());
        order.setPaymentStatus(PaymentStatus.SUCCESS);
    }

    private void withdrawAmountFromCustomer(@NotNull Order order) {
        TransactionResponse customerTransactionResponse = bankService.withdraw(order.getCustomerId(), getTransactionValue(order));
        order.setCustomerTransactionId(customerTransactionResponse.getTransactionId());
    }

    private double getTransactionValue(@NotNull Order order){
        if (order.isCouponApplied()){
            return order.getTotalAmount()-order.getDiscountAmount();
        }
        return order.getTotalAmount();
    }

    private void consumeStock(@NotNull Order order) {
        storeService.consumeStock(order.getOrderItems()) ;
    }

    private void consumeCoupon(@NotNull Order order) {
        if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            CouponResponse couponResponse = couponService.consumeCoupon(order.getCouponCode(), order.getTotalAmount());
            order.setDiscountAmount(order.getTotalAmount() - couponResponse.getAmount());
            order.setCouponApplied(true);
            log.info("Order got discount value -> {}", order.getDiscountAmount());
        }
    }
}
