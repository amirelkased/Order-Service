package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.TransactionIdInvalidException;
import com.fawry.orderservice.model.dto.CouponRequest;
import com.fawry.orderservice.model.dto.CouponResponse;
import com.fawry.orderservice.thirdparty.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    @Value("${coupon.api.url}")
    private String BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public CouponResponse consumeCoupon(String couponCode, double amount, Long customerId, String transactionId) {
        CouponRequest couponRequest = CouponRequest.builder()
                .couponCode(couponCode)
                .amount(amount)
                .customerId(customerId)
                .transactionId(transactionId)
                .build();
        try {
            ResponseEntity<CouponResponse> couponResponse =
                    restTemplate.postForEntity(BASE_URL.concat("/consume"), couponRequest, CouponResponse.class);
            return couponResponse.getBody();
        } catch (HttpClientErrorException e) {
            String message = "Coupon code '%s'!".formatted(couponCode);
            if (e.getStatusCode().is4xxClientError()) {
                CouponResponse couponResponse = e.getResponseBodyAs(CouponResponse.class);
                assert couponResponse != null;
                message = message.concat(couponResponse.getMessage());
            }
            log.error(message);
            throw new TransactionIdInvalidException(message);
        }
    }

    @Override
    public void unconsumeCoupon(String transactionId) {
        try {
//            String baseUrl = BASE_URL.concat("/unconsume/").concat(transactionId);
            String baseUrl = "http://localhost:8080/coupon/unconsume/"+transactionId;
            restTemplate.postForEntity(baseUrl, null, CouponResponse.class);
        } catch (HttpClientErrorException e) {
            String message = "No Coupon Transaction '%s'!".formatted(transactionId);
            if (e.getStatusCode().is4xxClientError()) {
                CouponResponse couponResponse = e.getResponseBodyAs(CouponResponse.class);
                assert couponResponse != null;
                message = message.concat(couponResponse.getMessage());
            }
            log.error(message);
            throw new TransactionIdInvalidException(message);
        }
    }
}