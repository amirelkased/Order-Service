package com.fawry.orderservice.exception;

public class TransactionIdInvalidException extends RuntimeException {
    public TransactionIdInvalidException(String message) {
        super(message);
    }
}
