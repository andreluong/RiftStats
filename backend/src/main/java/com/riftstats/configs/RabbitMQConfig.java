package com.riftstats.configs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@Getter
@Slf4j
public class RabbitMQConfig {
    private final String playerQueueName;
    private final String playerExchangeName;
    private final String playerRoutingKey;
    private final String matchExchangeName;
    private final MatchQueueConfig matchQueueConfig;

    public RabbitMQConfig(@Value("${rabbitmq.player.queue.name}") String playerQueueName,
                          @Value("${rabbitmq.player.exchange.name}") String playerExchangeName,
                          @Value("${rabbitmq.player.routing.key}") String playerRoutingKey,
                          @Value("${rabbitmq.match.exchange}") String matchExchangeName,
                          MatchQueueConfig matchQueueConfig
    ) {
        this.playerQueueName = playerQueueName;
        this.playerExchangeName = playerExchangeName;
        this.playerRoutingKey = playerRoutingKey;
        this.matchExchangeName = matchExchangeName;
        this.matchQueueConfig = matchQueueConfig;
    }

    @Bean
    public Queue playerQueue() {
        return new Queue(playerQueueName, true);
    }

    @Bean
    public TopicExchange playerExchange() {
        return new TopicExchange(playerExchangeName, true, false);
    }

    @Bean
    public Binding playerBinding(@Qualifier("playerExchange") TopicExchange exchange,
                                 @Qualifier("playerQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(playerRoutingKey);
    }

    @Bean
    public TopicExchange matchExchange() {
        return new TopicExchange(matchExchangeName, true, false);
    }

    @Bean
    public Declarables matchRegionGroupDeclarableList(TopicExchange matchExchange) {
        List<Queue> queues = matchQueueConfig.getQueues().values().stream()
                .map(name -> QueueBuilder.durable(name).build())
                .toList();

        List<Binding> bindings = queues.stream()
                .map(queue -> {
                    String regionGroup = matchQueueConfig.getQueues().entrySet().stream()
                            .filter(e -> e.getValue().equals(queue.getName()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElseThrow(() -> {
                                log.error("Invalid region found for queue={}", queue.getName());
                                return new IllegalStateException("Invalid region found for queue=" + queue.getName());
                            });
                    String routingKey = matchQueueConfig.getRoutingKeys().get(regionGroup);
                    return BindingBuilder.bind(queue).to(matchExchange).with(routingKey);
                })
                .toList();

        List<Declarable> declarableList = new ArrayList<>();
        declarableList.addAll(queues);
        declarableList.addAll(bindings);
        return new Declarables(declarableList);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMissingQueuesFatal(false);
        factory.setAutoStartup(true);
        factory.setPrefetchCount(10);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
