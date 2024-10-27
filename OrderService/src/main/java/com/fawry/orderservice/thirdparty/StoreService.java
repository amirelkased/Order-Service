package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.StockRequest;

import java.util.List;

public interface StoreService {
    void consumeStock(List<StockRequest> orderItems);
}
