package com.fawry.orderservice.service;


import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.PaymentStatus;
import com.fawry.orderservice.model.dto.CouponResponse;
import com.fawry.orderservice.model.dto.TransactionResponse;
import com.fawry.orderservice.service.thirdparty.BankService;
import com.fawry.orderservice.service.thirdparty.CouponService;
import com.fawry.orderservice.service.thirdparty.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCoordinator {
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;


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
