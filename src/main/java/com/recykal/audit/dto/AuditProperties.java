package com.recykal.audit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recykal.audit.enums.AuditPointcutType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "audit")
public class AuditProperties {

    private Boolean enabled = false;

    @JsonProperty("pointcut_strategy")
    private AuditPointcutType auditPointcutType = AuditPointcutType.REQUEST_MAPPING;

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
