package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.dto.CouponRequest;
import com.fawry.orderservice.model.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CouponService {
    public static final String BASE_URL = "http://localhost:8080/api/v1/coupons/consume";
    private final RestTemplate restTemplate = new RestTemplate();


    public CouponResponse consumeCoupon(String couponCode, double amount) {
        CouponRequest couponRequest = CouponRequest.builder()
                .couponCode(couponCode)
                .amount(amount)
                .build();
        ResponseEntity<CouponResponse> couponResponse=
                restTemplate.postForEntity(BASE_URL, couponRequest, CouponResponse.class);
        return couponResponse.getBody();
    }
}