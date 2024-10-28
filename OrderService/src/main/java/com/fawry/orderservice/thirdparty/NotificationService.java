package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendOrderNotification(NotificationRequest notificationRequest);

    void sendOrderNotification(Order order);
}
