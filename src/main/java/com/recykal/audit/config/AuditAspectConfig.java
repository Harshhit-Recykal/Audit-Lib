package com.recykal.audit.config;

import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.RabbitMQProperties;
import com.recykal.audit.service.AuditLogging;
import jakarta.persistence.EntityManager;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAspectConfig {
    @Bean
    public AuditLogging auditLogging(@Qualifier(Constants.RABBITMQ_CONSTANTS.AMQ_TEMPLATE) RabbitTemplate rabbitTemplate,
                                     EntityManager entityManager,
                                     RabbitMQProperties rabbitMQProperties) {
        return new AuditLogging(rabbitTemplate, entityManager, rabbitMQProperties);
    }

}
