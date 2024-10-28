package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.dto.StockRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface StoreService {
    void consumeStock(List<StockRequest> orderItems);
}
