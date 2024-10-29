package com.fawry.orderservice.mocking;


import com.fawry.orderservice.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("")
@RequiredArgsConstructor
public class MockRestController {
    public static final Random RANDOM = new SecureRandom();

    @PostMapping("api/v1/banking/transaction/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest transactionRequest) {
        // happy
        log.info("Bank API: withdraw from {} amount {}", transactionRequest.getUserId(), transactionRequest.getAmount());
        return ResponseEntity.ok(
                TransactionResponse.builder()
                        .status("success")
                        .message("MOCKING: withdraw success")
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

    @PostMapping("api/v1/banking/transaction/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        log.info("Bank API: deposit to {} amount {}", transactionRequest.getUserId(), transactionRequest.getAmount());

        return ResponseEntity.ok(
                TransactionResponse.builder()
                        .status("success")
                        .message("MOCKING: deposit success")
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

    @PostMapping("api/v1/banking/transaction/refund/{id}")
    public ResponseEntity<Void> refundTransaction(@PathVariable("id") String transactionId) {
        // happy
        log.info("Bank Service: Refunding Transaction id {}", transactionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("api/v1/products/skus")
    public ResponseEntity<ProductsWithPriceResponse> getProductsAlongPrices(@RequestBody List<String> skus) {
        log.info("Request Skus and return along prices {}", skus);
        return ResponseEntity.ok(
                ProductsWithPriceResponse.builder()
                        .status("success")
                        .message("MOCKING: map products with prices")
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

    @PostMapping("coupon/consume")
    public ResponseEntity<CouponResponse> consumeCoupon(@RequestBody CouponRequest couponRequest) {
        log.info("Mock Coupon consume api with couponCode {} and amount {}", couponRequest.getCouponCode(), couponRequest.getAmount());
        // happy
        return ResponseEntity.ok(
                CouponResponse.builder()
                        .status("success")
                        .message("MOCKING: Coupon valid")
                        .amount(couponRequest.getAmount() - (couponRequest.getAmount() * .10))
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

    @PostMapping("coupon/unconsume/{id}")
    public ResponseEntity<CouponResponse> unconsumeCoupon(@PathVariable String id) {
        log.info("Mock Coupon unconsume api with trans id {}",id);
        // happy
        return ResponseEntity.ok(
                CouponResponse.builder()
                        .status("success")
                        .message("MOCKING: unconsume Coupon valid")
                        .amount(0)
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


    @PostMapping("stocks/consume")
    public ResponseEntity<Void> consumeProducts(@RequestBody List<StockRequest> orderItems) {
        log.info("Mocking consuming order's products -> {}", orderItems);
        // happy
        return ResponseEntity.ok().build();
        //sad
//        return ResponseEntity.badRequest().build();
    }

    @PostMapping("stocks/unconsume")
    public ResponseEntity<Void> unconsumeProducts(@RequestBody List<StockRequest> orderItems) {
        log.info("Mocking unconsuming order's products -> {}", orderItems);
        // happy
        return ResponseEntity.ok().build();
        //sad
//        return ResponseEntity.badRequest().build();
    }
}
