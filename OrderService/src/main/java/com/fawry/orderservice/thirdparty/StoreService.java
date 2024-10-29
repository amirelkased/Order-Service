package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StoreService {
    void consumeStock(List<OrderItem> orderItems);

    void releaseStock(List<OrderItem> orderItems);
}
