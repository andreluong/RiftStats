package com.riftstats.configs;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQInitializer {
    private final RabbitAdmin rabbitAdmin;
    private final RabbitMQConfig rabbitMQConfig;

    @PostConstruct
    public void init() {
        rabbitAdmin.declareExchange(rabbitMQConfig.matchExchange());
        rabbitAdmin.declareExchange(rabbitMQConfig.playerExchange());
        rabbitAdmin.declareQueue(rabbitMQConfig.playerQueue());
    }
}
