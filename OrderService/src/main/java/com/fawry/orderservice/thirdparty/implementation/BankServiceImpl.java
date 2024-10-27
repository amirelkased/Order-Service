package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.PaymentFailedException;
import com.fawry.orderservice.model.dto.TransactionRequest;
import com.fawry.orderservice.model.dto.TransactionResponse;
import com.fawry.orderservice.thirdparty.BankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {
    private static final String BASE_URL = "http://localhost:8081/api/v1/bank";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TransactionResponse withdraw(Long customerId, double amount) {
        TransactionRequest transactionRequest = prepareRequestPayload(customerId, amount);

        try {
            ResponseEntity<TransactionResponse> transactionResponse =
                    restTemplate.postForEntity(BASE_URL.concat("/withdraw"), transactionRequest, TransactionResponse.class);
            return transactionResponse.getBody();
        } catch (HttpClientErrorException e) {
            String message = "Withdraw Proccess Failed from customer: %d, With message ".formatted(customerId);
            if (e.getStatusCode().is4xxClientError()) {
                TransactionResponse transactionResponse = e.getResponseBodyAs(TransactionResponse.class);
                assert transactionResponse != null;
                message = message.concat(transactionResponse.getMessage());
            }
            log.error(message);
            throw new PaymentFailedException(message);
        }
    }

    @Override
    public TransactionResponse deposit(Long merchantId, double amount) {
        TransactionRequest transactionRequest = prepareRequestPayload(merchantId, amount);
        try {
            ResponseEntity<TransactionResponse> transactionResponse =
                    restTemplate.postForEntity(BASE_URL.concat("/deposit"), transactionRequest, TransactionResponse.class);
            return transactionResponse.getBody();
        } catch (HttpClientErrorException e) {
            String message = "Deposit Proccess Failed to Merchant %d!, With message ".formatted(merchantId);
            if (e.getStatusCode().is4xxClientError()) {
                TransactionResponse transactionResponse = e.getResponseBodyAs(TransactionResponse.class);
                assert transactionResponse != null;
                message = message.concat(transactionResponse.getMessage());
            }
            log.error(message);
            throw new PaymentFailedException(message);
        }
    }

    private TransactionRequest prepareRequestPayload(Long customerId, double amount) {
        return TransactionRequest.builder()
                .userId(customerId)
                .amount(amount)
                .build();
    }
}
