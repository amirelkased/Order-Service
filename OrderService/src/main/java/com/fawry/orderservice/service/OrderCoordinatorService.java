package com.fawry.orderservice.service;

import com.fawry.orderservice.exception.OrderFailedException;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.thirdparty.BankService;
import com.fawry.orderservice.thirdparty.CouponService;
import com.fawry.orderservice.thirdparty.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class OrderCoordinatorService {
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;

    @Autowired
    protected OrderCoordinatorService(CouponService couponService, StoreService storeService, BankService bankService) {
        this.couponService = couponService;
        this.storeService = storeService;
        this.bankService = bankService;
    }

    public final void processOrder(Order order) {
        try {
            consumeCoupon(order);
            order.setOrderStatus(OrderStatus.COUPON_APPLIED);
            consumeStock(order);
            order.setOrderStatus(OrderStatus.STOCK_CONSUMED);
            withdrawAmountFromCustomer(order);
            order.setOrderStatus(OrderStatus.CUSTOMER_WITHDRAWN);
            depositAmountToMerchant(order);
            order.setOrderStatus(OrderStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Error processing order, initiating rollback: {}", e.getMessage());
            rollbackOrder(order);
            order.setOrderStatus(OrderStatus.FAILED);
            throw new OrderFailedException(e.getMessage());
        }
    }

    public final void rollbackOrder(@NotNull Order order) {
        switch (order.getOrderStatus()) {
            case CUSTOMER_WITHDRAWN:
                refundCustomer(order);
            case STOCK_CONSUMED:
                restockItems(order);
            case COUPON_APPLIED:
                releaseCoupon(order);
                break;
            default:
                log.warn("No rollback actions necessary");
        }
    }

    public void releaseCoupon(@NotNull Order order) {
        couponService.unconsumeCoupon(order.getTransactionId());
    }

    public void restockItems(@NotNull Order order) {
        storeService.releaseStock(order.getOrderItems());
    }

    public void refundCustomer(@NotNull Order order) {
        bankService.refund(order.getCustomerTransactionId());
    }

    public abstract void depositAmountToMerchant(@NotNull Order order);

    public abstract void withdrawAmountFromCustomer(@NotNull Order order);

    public abstract void consumeStock(@NotNull Order order);

    public abstract void consumeCoupon(@NotNull Order order);
}
