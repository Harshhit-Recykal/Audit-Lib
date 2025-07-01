package com.recykal.audit.dto;

import com.recykal.audit.enums.AuditPointcutType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

    private Boolean enabled = false;

    private AuditPointcutType pointcutType = AuditPointcutType.REQUEST_MAPPING;

    private RabbitMQ rabbitmq;

    @Data
    public static class RabbitMQ {
        private String host;
        private int port;
        private String username;
        private String password;
        private String queue;
        private String exchange;
        private String routingKey;
    }

}
