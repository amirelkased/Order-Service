package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.CouponResponse;

public interface CouponService {
    CouponResponse consumeCoupon(String couponCode, double amount);
}
