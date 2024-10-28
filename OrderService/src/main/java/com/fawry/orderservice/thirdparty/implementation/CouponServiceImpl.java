package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.exception.CouponInvalidException;
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