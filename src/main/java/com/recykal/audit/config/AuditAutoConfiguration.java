package com.recykal.audit.config;

import com.recykal.audit.dto.AuditProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "audit",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties({AuditProperties.class})
@Import({AuditAspectConfig.class, RabbitMQConfig.class, SerialisationConfig.class})
public class AuditAutoConfiguration {

}
