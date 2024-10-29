package com.fawry.orderservice.service.implementation;


import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.PaymentStatus;
import com.fawry.orderservice.model.dto.CouponResponse;
import com.fawry.orderservice.model.dto.TransactionResponse;
import com.fawry.orderservice.service.OrderCoordinatorService;
import com.fawry.orderservice.thirdparty.BankService;
import com.fawry.orderservice.thirdparty.CouponService;
import com.fawry.orderservice.thirdparty.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderCoordinatorServiceImpl extends OrderCoordinatorService {
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;

    @Autowired
    public OrderCoordinatorServiceImpl(CouponService couponService, StoreService storeService, BankService bankService) {
        super(couponService, storeService, bankService);
        this.couponService = couponService;
        this.storeService = storeService;
        this.bankService = bankService;
    }

    @Override
    public void depositAmountToMerchant(@NotNull Order order) {
        TransactionResponse merchantTransactionResponse = bankService.deposit(order.getMerchant(), getTransactionValue(order));
        order.setMerchantTransactionId(merchantTransactionResponse.getTransactionId());
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        log.info("Deposit Done");
    }

    @Override
    public void withdrawAmountFromCustomer(@NotNull Order order) {
        TransactionResponse customerTransactionResponse = bankService.withdraw(order.getCustomerId(), getTransactionValue(order));
        order.setCustomerTransactionId(customerTransactionResponse.getTransactionId());
        log.info("Withdraw Done");
    }

    private double getTransactionValue(@NotNull Order order) {
        if (order.isCouponApplied()) {
            return order.getTotalAmount() - order.getDiscountAmount();
        }
        return order.getTotalAmount();
    }

    @Override
    public void consumeStock(@NotNull Order order) {
        storeService.consumeStock(order.getOrderItems());
        log.info("Stock consuming is Done");
    }

    @Override
    public void consumeCoupon(@NotNull Order order) {
        if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            CouponResponse couponResponse = couponService.consumeCoupon(
                    order.getCouponCode(),
                    order.getTotalAmount(),
                    order.getCustomerId(),
                    order.getTransactionId()
            );
            order.setDiscountAmount(order.getTotalAmount() - couponResponse.getAmount());
            order.setCouponApplied(true);
            log.info("Order got discount value -> {}", order.getDiscountAmount());
        }
    }
}
