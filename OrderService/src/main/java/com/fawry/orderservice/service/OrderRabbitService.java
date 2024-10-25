package com.fawry.orderservice.service;

import com.fawry.orderservice.model.Order;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderRabbitService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.order.creation.routing.key}")
    private String routingKey;
    private final RabbitTemplate rabbitTemplate;
    public static final Logger logger = LoggerFactory.getLogger(OrderRabbitService.class);

    public void sendOrderToQueue(Order order){
        logger.info("Order Creation -> {}", order);
        rabbitTemplate.convertAndSend(exchange, routingKey, order);
    }
}
