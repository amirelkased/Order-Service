package com.fawry.orderservice.exception;

public class RefundFailedException extends RuntimeException {
    public RefundFailedException(String message) {
        super(message);
    }
}
