package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.exception.CouponInvalidException;
import com.fawry.orderservice.model.dto.CouponRequest;
import com.fawry.orderservice.model.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private static final String BASE_URL = "http://localhost:8081/api/v1/coupons/consume";
    private final RestTemplate restTemplate = new RestTemplate();


    public CouponResponse consumeCoupon(String couponCode, double amount) {
        CouponRequest couponRequest = CouponRequest.builder()
                .couponCode(couponCode)
                .amount(amount)
                .build();
        try {
            ResponseEntity<CouponResponse> couponResponse =
                    restTemplate.postForEntity(BASE_URL, couponRequest, CouponResponse.class);
            return couponResponse.getBody();
        } catch (HttpClientErrorException e) {
            String message = "Coupon code '%s'!".formatted(couponCode);
            if (e.getStatusCode().is4xxClientError()) {
                CouponResponse couponResponse = e.getResponseBodyAs(CouponResponse.class);
                assert couponResponse != null;
                message = message.concat(couponResponse.getMessage());
            }
            log.error(message);
            throw new CouponInvalidException(message);
        }
    }
}