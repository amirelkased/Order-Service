package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.CouponResponse;
import org.springframework.stereotype.Service;

@Service
public interface CouponService {
    CouponResponse consumeCoupon(String couponCode, double amount, Long customerId, String transactionId);

    void unconsumeCoupon(String transactionId);
}
