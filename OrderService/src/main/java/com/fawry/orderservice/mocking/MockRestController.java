package com.fawry.orderservice.mocking;


import com.fawry.orderservice.model.OrderItem;
import com.fawry.orderservice.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class MockRestController {
    public static final Random RANDOM = new SecureRandom();

    @PostMapping("bank/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest transactionRequest) {
        // happy
        log.info("Bank API: withdraw from {} amount {}", transactionRequest.getUserId(), transactionRequest.getAmount());
//        return ResponseEntity.ok(
//                TransactionResponse.builder()
//                        .status(true)
//                        .transactionId(UUID.randomUUID().toString())
//                        .build()
//        );
        // sad
        return ResponseEntity.badRequest().body(
                TransactionResponse.builder()
                        .status(false)
//                        .transactionId(UUID.randomUUID().toString())
                        .build()
        );
    }

    @PostMapping("bank/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        log.info("Bank API: deposit to {} amount {}", transactionRequest.getUserId(), transactionRequest.getAmount());

        // happy
        return ResponseEntity.badRequest().body(
                TransactionResponse.builder()
                        .status(true)
                        .transactionId(UUID.randomUUID().toString())
                        .build()
        );
        // sad
//        return ResponseEntity.ok(
//                TransactionResponse.builder()
//                        .status(false)
////                        .transactionId(UUID.randomUUID().toString())
//                        .build()
//        );
    }

    @PostMapping("products/skus")
    public ResponseEntity<ProductDto[]> getProductsAlongPrices(@RequestBody List<String> skus){
        log.info("Request Skus and return along prices {}", skus);
        return ResponseEntity.ok(
                skus.stream().map(
                        sku -> ProductDto.builder().price(RANDOM.nextDouble()).sku(sku).build()
                ).toArray(ProductDto[]::new)
        );
    }

    @PostMapping("coupons/consume")
    public ResponseEntity<CouponResponse> consumeCoupon(@RequestBody CouponRequest couponRequest){
        log.info("Mock Coupon consume api with couponCode {} and amount {}", couponRequest.getCouponCode(), couponRequest.getAmount());
        // happy
        return ResponseEntity.ok(
                CouponResponse.builder()
                        .status(true)
                        .amount(couponRequest.getAmount() - (couponRequest.getAmount()*.10))
                        .build()
        );
        // sad
//        return ResponseEntity.badRequest().body(
//                CouponResponse.builder()
//                        .status(false)
//                        .amount(couponRequest.getAmount())
//                        .build()
//        );
    }

    @PostMapping("store/stocks")
    public ResponseEntity<Void> consumeProducts(@RequestBody List<OrderItem> orderItems){
        log.info("Mocking consuming order's products -> {}", orderItems);
        // happy
        return ResponseEntity.ok().build();
        //sad
//        return ResponseEntity.badRequest().build();
    }


}
