package com.fawry.orderservice.controller;

import com.fawry.orderservice.common.ResponsePage;
import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.CreateOrderRequest;
import com.fawry.orderservice.service.OrderCoordinatorService;
import com.fawry.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final OrderCoordinatorService orderCoordinatorService;

    @GetMapping("{customer}")
    public ResponseEntity<ResponsePage<Order>> getAllOrders(
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            Pageable pageable,
            @PathVariable Long customer){
        return ResponseEntity.ok(
          orderService.getAllOrdersByCustomerIdAndRangeDate(pageable, customer, from, to)
        );
    }

    @GetMapping("{customerId}/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long customerId, @PathVariable Long orderId){
        return ResponseEntity.ok(
          orderService.getOrderById(customerId, orderId)
        );
    }

    @PostMapping("")
    public ResponseEntity<Order> createOrder(
            @RequestBody CreateOrderRequest orderRequest
            ){
        return ResponseEntity.ok(
                orderCoordinatorService.createOrder(orderRequest)
        );
    }
}
