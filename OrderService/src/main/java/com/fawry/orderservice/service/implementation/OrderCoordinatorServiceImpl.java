package com.fawry.orderservice.service.implementation;


import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.PaymentStatus;
import com.fawry.orderservice.model.dto.*;
import com.fawry.orderservice.service.OrderCoordinatorService;
import com.fawry.orderservice.thirdparty.BankService;
import com.fawry.orderservice.thirdparty.CouponService;
import com.fawry.orderservice.thirdparty.ProductService;
import com.fawry.orderservice.thirdparty.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fawry.orderservice.model.mapper.OrderItemsMapper.mapToOrderItems;
import static com.fawry.orderservice.model.mapper.OrderItemsMapper.mapToStockRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCoordinatorServiceImpl implements OrderCoordinatorService {
    @Value("${order.merchant.id}")
    private Long merchantId;
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;
    private final ProductService productService;

    @NotNull
    @Override
    public Order prepareOrderFromRequest(@NotNull CreateOrderRequest request) {
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

    @Override
    public Order processOrder(Order order) {
        consumeCoupon(order);
        log.info("Consume Coupon Success");

        consumeStock(order);
        log.info("Consume Stock Success");

        withdrawAmountFromCustomer(order);
        log.info("Withdraw Success");

        depositAmountToMerchant(order);
        log.info("Deposite Success");

        order.setOrderStatus(OrderStatus.COMPLETED);
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

    private double getTransactionValue(@NotNull Order order) {
        if (order.isCouponApplied()) {
            return order.getTotalAmount() - order.getDiscountAmount();
        }
        return order.getTotalAmount();
    }

    private void consumeStock(@NotNull Order order) {
        List<StockRequest> stockRequestList = mapToStockRequest(order.getOrderItems());
        storeService.consumeStock(stockRequestList);
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


