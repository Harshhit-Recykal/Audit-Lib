package com.recykal.audit.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "audit.rabbitmq")
public class RabbitMQProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String queue;
    private String exchange;
    private String routingKey;

}
