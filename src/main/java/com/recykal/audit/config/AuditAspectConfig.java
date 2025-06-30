package com.recykal.audit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.AuditProperties;
import com.recykal.audit.dto.EntityMatching;
import com.recykal.audit.service.AuditLoggingAspect;
import com.recykal.audit.service.AuditLoggingHandler;
import com.recykal.audit.service.strategy.AuditableAnnotationAuditStrategy;
import com.recykal.audit.service.strategy.RequestMappingAuditStrategy;
import com.recykal.audit.service.strategy.ServiceClassAuditStrategy;
import jakarta.persistence.EntityManager;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAspectConfig {

    @Bean
    public AuditLoggingHandler auditHandler(@Qualifier(Constants.RABBITMQ_CONSTANTS.AMQ_TEMPLATE) AmqpTemplate amqpTemplate,  @Qualifier(Constants.CONFIG_CONSTANTS.AUDIT_OBJECT_MAPPER) ObjectMapper objectMapper,
                                            EntityManager entityManager,
                                            EntityMatching entityMatching,
                                            AuditProperties auditProperties) {
        return new AuditLoggingHandler(amqpTemplate, entityManager, objectMapper, entityMatching, auditProperties);
    }

    @Bean
    public RequestMappingAuditStrategy requestMappingAuditStrategy(AuditLoggingHandler auditHandler, AuditProperties auditProperties) {
        return new RequestMappingAuditStrategy(auditHandler, auditProperties);
    }

    @Bean
    public ServiceClassAuditStrategy serviceClassAuditStrategy(AuditLoggingHandler auditHandler, AuditProperties auditProperties) {
        return new ServiceClassAuditStrategy(auditHandler, auditProperties);
    }

    @Bean
    public AuditableAnnotationAuditStrategy auditableAnnotationAuditStrategy(AuditLoggingHandler auditHandler, AuditProperties auditProperties) {
        return new AuditableAnnotationAuditStrategy(auditHandler, auditProperties);
    }

    @Bean
    public AuditLoggingAspect auditAspect(RequestMappingAuditStrategy requestMappingAuditStrategy,
                                          ServiceClassAuditStrategy serviceClassAuditStrategy,
                                          AuditableAnnotationAuditStrategy auditableAnnotationAuditStrategy) {
        return new AuditLoggingAspect(requestMappingAuditStrategy, serviceClassAuditStrategy, auditableAnnotationAuditStrategy);
    }

}
