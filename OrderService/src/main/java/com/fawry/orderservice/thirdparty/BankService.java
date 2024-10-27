package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.TransactionResponse;

public interface BankService {
    TransactionResponse withdraw(Long customerId, double amount);

    TransactionResponse deposit(Long merchantId, double amount);
}
