package com.fawry.orderservice.service.thirdparty;

import com.fawry.orderservice.model.Order;
import com.fawry.orderservice.model.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.notification.routing.key}")
    private String notificationRoutingKey;
    @Value("${rabbitmq.notification.queue}")
    private String queue;
    private final RabbitTemplate rabbitTemplate;
    private final RetryTemplate retryTemplate;

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

    @Async
    public void sendOrderNotification(Order order) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .order(order)
                .build();
        sendOrderNotification(notificationRequest);
    }

    @RabbitListener(queues = "${rabbitmq.notification.queue}")
    public void listen(NotificationRequest notificationRequest){
        log.info("I receive notification {}", notificationRequest);
        log.info("Order details {}", notificationRequest.getOrder());
        log.info("Dates create{} last{}", notificationRequest.getOrder().getCreatedAt(),notificationRequest.getOrder().getLastModifiedAt());
    }
}
