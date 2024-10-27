package com.fawry.orderservice.thirdparty.implementation;

import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.NotificationRequest;
import com.fawry.orderservice.thirdparty.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.notification.routing.key}")
    private String notificationRoutingKey;
    private final RabbitTemplate rabbitTemplate;
    private final RetryTemplate retryTemplate;

    @Override
    @Async
    public void sendOrderNotification(NotificationRequest notificationRequest) {
        retryTemplate.execute(context -> {
            try {
                rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, notificationRequest);
                log.info("Notification sended successfully...");
                return null;
            } catch (AmqpException e) {
                log.error("Failed to send notification, retrying...");
                throw e;
            }
        });
    }

    @Override
    @Async
    public void sendOrderNotification(Order order) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .order(order)
                .build();
        sendOrderNotification(notificationRequest);
    }
}
