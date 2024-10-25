package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.notification.routing.key}")
    private String notificationRoutingKey;
    private final RabbitTemplate rabbitTemplate;

    public void sendOrderNotification(Order order) {
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, order);
    }
}
