package com.riftstats.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rabbitmq.match")
@Getter
@Setter
public class MatchQueueConfig {
    private Map<String, String> queues;
    private Map<String, String> routingKeys;
}
