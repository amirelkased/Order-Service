package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public interface BankService {
    TransactionResponse withdraw(Long customerId, double amount);

    TransactionResponse deposit(Long merchantId, double amount);

    void refund(String customerTransactionId);
}
