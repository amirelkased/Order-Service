package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.dto.TransactionRequest;
import com.fawry.orderservice.model.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {
    public static final String BASE_URL = "http://localhost:8080/api/v1/bank";
    private final RestTemplate restTemplate = new RestTemplate();

    public TransactionResponse withdraw(Long customerId, double amount) {
        TransactionRequest transactionRequest = prepareRequestPayload(customerId, amount);

        try {
            ResponseEntity<TransactionResponse> transactionResponse =
                    restTemplate.postForEntity(BASE_URL.concat("/withdraw"), transactionRequest, TransactionResponse.class);
            return transactionResponse.getBody();
        } catch (Exception ex) {
            log.error("Their happen exception during withdraw");
            return TransactionResponse.builder()
                    .status(false)
                    .build();
        }
    }

    public TransactionResponse deposit(Long merchantId, double amount) {
        TransactionRequest transactionRequest = prepareRequestPayload(merchantId, amount);
        ResponseEntity<TransactionResponse> transactionResponse =
                restTemplate.postForEntity(BASE_URL.concat("/deposit"), transactionRequest, TransactionResponse.class);
        return transactionResponse.getBody();
    }

    private TransactionRequest prepareRequestPayload(Long customerId, double amount) {
        return TransactionRequest.builder()
                .userId(customerId)
                .amount(amount)
                .build();
    }
}
