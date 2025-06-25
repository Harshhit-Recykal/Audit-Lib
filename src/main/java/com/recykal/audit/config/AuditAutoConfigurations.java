package com.recykal.audit.config;

import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.RabbitMQProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@ConditionalOnProperty(prefix = Constants.CONFIG_CONSTANTS.AUDIT, name = Constants.CONFIG_CONSTANTS.ENABLED, havingValue = Constants.CONFIG_CONSTANTS.TRUE)
@EnableConfigurationProperties(RabbitMQProperties.class)
public class AuditAutoConfigurations {

}
