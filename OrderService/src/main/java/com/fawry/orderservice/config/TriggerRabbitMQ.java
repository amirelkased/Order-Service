package com.fawry.orderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TriggerRabbitMQ {

    @Bean
    public CommandLineRunner commandLineRunner(@NotNull RabbitTemplate rabbitTemplate) {
        return args -> {
            try {
                rabbitTemplate.convertAndSend("", "", "Connection Test");
                log.info("Rabbit triggering successfully!");
            } catch (AmqpException e) {
                log.error("Error when triggering rabbit connection!");
            }
        };
    }
}
