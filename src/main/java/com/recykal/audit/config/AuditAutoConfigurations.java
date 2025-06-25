package com.recykal.audit.config;

import com.recykal.audit.dto.RabbitMQProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "audit",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(RabbitMQProperties.class)
public class AuditAutoConfigurations {

}
