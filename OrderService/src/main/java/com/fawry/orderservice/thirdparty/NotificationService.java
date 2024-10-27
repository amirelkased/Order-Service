package com.fawry.orderservice.thirdparty;

import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.NotificationRequest;

public interface NotificationService {
    void sendOrderNotification(NotificationRequest notificationRequest);

    void sendOrderNotification(Order order);
}
