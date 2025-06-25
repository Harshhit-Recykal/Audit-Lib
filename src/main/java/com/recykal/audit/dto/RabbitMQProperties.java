package com.recykal.audit.dto;

import com.recykal.audit.constants.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.rabbitmq")
@Data
public class RabbitMQProperties {
    private String queue;
    private String exchange;
    private String routingKey;
}
