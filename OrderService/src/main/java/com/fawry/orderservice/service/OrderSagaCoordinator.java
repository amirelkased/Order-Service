package com.fawry.orderservice.service;


import com.fawry.orderservice.exception.CouponInvalidException;
import com.fawry.orderservice.exception.OutOfStockException;
import com.fawry.orderservice.exception.PaymentFailedException;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.OrderStatus;
import com.fawry.orderservice.model.PaymentStatus;
import com.fawry.orderservice.model.dto.CouponResponse;
import com.fawry.orderservice.model.dto.TransactionResponse;
import com.fawry.orderservice.repository.OrderRepository;
import com.fawry.orderservice.service.thirdparty.BankService;
import com.fawry.orderservice.service.thirdparty.CouponService;
import com.fawry.orderservice.service.thirdparty.NotificationService;
import com.fawry.orderservice.service.thirdparty.StoreService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderSagaCoordinator {
    public static final Logger logger = LoggerFactory.getLogger(OrderSagaCoordinator.class);
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    private final DeadOrderRabbitService deadOrderRabbitService;

    public void handleOrderCreation(Order order) {
        try {
            processOrder(order);
        } catch (Exception e) {
            logger.error("Error processing order: {}", order.getId(), e);
            deadOrderRabbitService.sendToDeadLetterQueue(order);
        }
    }

    private void processOrder(Order order) {
        if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            CouponResponse couponResponse = couponService.consumeCoupon(order.getCouponCode(), order.getTotalAmount());
            if (!couponResponse.isStatus()) {
                updateOrderStatus(order, OrderStatus.FAILED);
                throw new CouponInvalidException("Invalid coupon code: " + order.getCouponCode());
            }
            order.setDiscountAmount(order.getTotalAmount() - couponResponse.getAmount());
            order.setCouponApplied(true);
            logger.info("Order got discount value -> {}", order.getDiscountAmount());
        }

        if (!storeService.consumeStock(order.getOrderItems())) {
            updateOrderStatus(order, OrderStatus.FAILED);
            throw new OutOfStockException("Some products are out of stock");
        }

        TransactionResponse customerTransactionResponse = bankService.withdraw(order.getCustomerId(), order.getTotalAmount());
        if (!customerTransactionResponse.isStatus()) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            updateOrderStatus(order, OrderStatus.FAILED);
            throw new PaymentFailedException("Payment failed when withdraw from customerId: " + order.getCustomerId());
        }
        order.setCustomerTransactionId(customerTransactionResponse.getTransactionId());

        TransactionResponse merchantTransactionResponse = bankService.deposit(order.getMerchant(), order.getTotalAmount());
        if (!merchantTransactionResponse.isStatus()) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            updateOrderStatus(order, OrderStatus.FAILED);
            throw new PaymentFailedException("Payment failed when deposite to merchantId: " + order.getMerchant());
        }
        order.setMerchantTransactionId(merchantTransactionResponse.getTransactionId());
        order.setPaymentStatus(PaymentStatus.SUCCESS);

        notificationService.sendOrderNotification(order);
        updateOrderStatus(order, OrderStatus.COMPLETED);
    }

    private void updateOrderStatus(@NotNull Order order, OrderStatus status) {
        order.setOrderStatus(status);
        orderRepository.save(order);
    }
}

/*
@Service
@RequiredArgsConstructor
public class OrderSagaCoordinator {
    public static final Logger logger =  LoggerFactory.getLogger(OrderSagaCoordinator.class);
    private final CouponService couponService;
    private final StoreService storeService;
    private final BankService bankService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;


    @RabbitListener(queues = "${rabbitmq.order.creation.queue}")
    public void handleOrderCreation(Order order) {
        try {
            if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
                CouponResponse couponResponse = couponService.consumeCoupon(order.getCouponCode(), order.getTotalAmount());
                if (!couponResponse.isStatus()) {
                    updateOrderStatus(order, OrderStatus.COUPON_INVALID);
                    return;
                }
                order.setDiscountAmount(order.getTotalAmount() - couponResponse.getAmount());
                order.setCouponApplied(true);
                logger.error("Order got discount value -> {}", order.getDiscountAmount());
            }

            boolean isStockAvailable = storeService.consumeStock(order.getOrderItems());
            if (!isStockAvailable) {
                logger.error("Their is product(s) out of stock");
                updateOrderStatus(order, OrderStatus.OUT_OF_STOCK);
                return;
            }

            TransactionResponse customerTransactionResponse = bankService.withdraw(order.getCustomerId(), order.getTotalAmount());
            if (customerTransactionResponse.isStatus()) {
                order.setCustomerTransactionId(customerTransactionResponse.getTransactionId());
                order.setPaymentStatus(PaymentStatus.SUCCESS);
            }else {
                logger.error("Their is error related to customer in bank");
                order.setPaymentStatus(PaymentStatus.FAILED);
                updateOrderStatus(order, OrderStatus.FAILED);
            }

            TransactionResponse merchantTransactionResponse = bankService.deposit(order.getMerchant(), order.getTotalAmount());
            if (merchantTransactionResponse.isStatus()) {
                order.setMerchantTransactionId(merchantTransactionResponse.getTransactionId());
            }else {
                logger.error("Their is error related to merchant in bank");
                order.setPaymentStatus(PaymentStatus.FAILED);
                updateOrderStatus(order, OrderStatus.FAILED);
            }

            logger.info("Last status of order before send notification {}", order);
            notificationService.sendOrderNotification(order);
            updateOrderStatus(order, OrderStatus.COMPLETED);
            logger.info("Last status of order after completed all steps successfully {}", order);
        } catch (Exception e) {
            logger.error( "Happen error at Order Coordinator -> {}",e.getMessage());
            updateOrderStatus(order, OrderStatus.FAILED);
        }
    }

    private void updateOrderStatus(@NotNull Order order, OrderStatus status) {
        order.setOrderStatus(status);
        orderRepository.save(order);
    }
}
*/
