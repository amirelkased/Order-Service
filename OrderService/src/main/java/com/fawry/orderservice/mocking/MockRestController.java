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
        return ResponseEntity.ok(
                TransactionResponse.builder()
                        .status("success")
                        .message("withdraw success")
                        .transactionId(UUID.randomUUID().toString())
                        .build()
        );
        // sad
//        return ResponseEntity.badRequest().body(
//                TransactionResponse.builder()
//                        .status("failed")
//                        .message("Customer haven't sufficient amount!")
////                        .transactionId(UUID.randomUUID().toString())
//                        .build()
//        );
    }

    @PostMapping("bank/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        log.info("Bank API: deposit to {} amount {}", transactionRequest.getUserId(), transactionRequest.getAmount());

        return ResponseEntity.ok(
                TransactionResponse.builder()
                        .status("success")
                        .message("deposit success")
                        .transactionId(UUID.randomUUID().toString())
                        .build()
        );
        // sad
//        return ResponseEntity.badRequest().body(
//                TransactionResponse.builder()
//                        .status("failed")
//                        .message("Deposit failed!")
////                        .transactionId(UUID.randomUUID().toString())
//                        .build()
//        );
    }

    @PostMapping("products/skus")
    public ResponseEntity<ProductsWithPriceResponse> getProductsAlongPrices(@RequestBody List<String> skus) {
        log.info("Request Skus and return along prices {}", skus);
        return ResponseEntity.ok(
                ProductsWithPriceResponse.builder()
                        .status("success")
                        .productDtos(skus.stream().map(
                        sku -> ProductDto.builder().price(RANDOM.nextDouble()).sku(sku).build()
                ).toList())
                        .build()
        );
//        ProductsWithPriceResponse response = new ProductsWithPriceResponse();
//        response.setStatus("failed");
//        response.setMessage("Their skus not correct");
//        return ResponseEntity.badRequest().body(
//                response
//        );
    }

    @PostMapping("coupons/consume")
    public ResponseEntity<CouponResponse> consumeCoupon(@RequestBody CouponRequest couponRequest) {
        log.info("Mock Coupon consume api with couponCode {} and amount {}", couponRequest.getCouponCode(), couponRequest.getAmount());
        // happy
        return ResponseEntity.ok(
                CouponResponse.builder()
                        .status("success")
                        .message("Coupon valid")
                        .amount(couponRequest.getAmount() - (couponRequest.getAmount()*.10))
                        .build()
        );
        // sad
//        CouponResponse coupon = new CouponResponse();
//        coupon.setStatus("failed");
//        coupon.setMessage("coupon is invalidate");
//        return ResponseEntity.badRequest().body(
//                coupon
//        );
    }

    @PostMapping("store/stocks")
    public ResponseEntity<Void> consumeProducts(@RequestBody List<OrderItem> orderItems) {
        log.info("Mocking consuming order's products -> {}", orderItems);
        // happy
        return ResponseEntity.ok().build();
        //sad
//        return ResponseEntity.badRequest().build();
    }
}
