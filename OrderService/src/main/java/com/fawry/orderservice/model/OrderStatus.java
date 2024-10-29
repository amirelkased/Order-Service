package com.fawry.orderservice.model;

public enum OrderStatus {
    NONE,
    COUPON_APPLIED,
    STOCK_CONSUMED,
    CUSTOMER_WITHDRAWN,
    MERCHANT_DEPOSITED,
    SUCCESS,
    FAILED
}
