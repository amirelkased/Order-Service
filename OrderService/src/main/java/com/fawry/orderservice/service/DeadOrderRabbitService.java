package com.fawry.orderservice.service;

import com.fawry.orderservice.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadOrderRabbitService {
    @Value("${rabbitmq.order.dead.letter.routing.key}")
    private String deadLetterRoutingKey;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    private final RabbitTemplate rabbitTemplate;

    public void sendToDeadLetterQueue(Order order){
        log.info("Send Order to Dead Queue order id :{}", order.getId());
        rabbitTemplate.convertAndSend(exchange, deadLetterRoutingKey, order);
    }
}
