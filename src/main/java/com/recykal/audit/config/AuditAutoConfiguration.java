package com.recykal.audit.config;

import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.RabbitMQProperties;
import com.recykal.audit.service.AuditLogging;
import jakarta.persistence.EntityManager;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "audit",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties({RabbitMQProperties.class})
public class AuditAutoConfiguration {

}
