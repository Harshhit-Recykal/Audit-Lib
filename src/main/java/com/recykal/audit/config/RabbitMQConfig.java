package com.recykal.audit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.AuditProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean(name = Constants.RABBITMQ_CONSTANTS.QUEUE)
    public Queue auditQueue(AuditProperties props) {
        return new Queue(props.getRabbitmq().getQueue(), true);
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.EXCHANGE)
    public TopicExchange auditExchange(AuditProperties props) {
        return new TopicExchange(props.getRabbitmq().getExchange(), true, false);
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.BINDING)
    public Binding auditBinding(@Qualifier(Constants.RABBITMQ_CONSTANTS.QUEUE) Queue queue,
                                @Qualifier(Constants.RABBITMQ_CONSTANTS.EXCHANGE) TopicExchange exchange,
                                AuditProperties props) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(props.getRabbitmq().getRoutingKey());
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.MESSAGE_CONVERTER)
    public MessageConverter auditMessageConverter(@Qualifier(Constants.CONFIG_CONSTANTS.AUDIT_OBJECT_MAPPER) ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.AMQ_TEMPLATE)
    public AmqpTemplate auditRabbitTemplate(ConnectionFactory connectionFactory,
                                            @Qualifier(Constants.RABBITMQ_CONSTANTS.MESSAGE_CONVERTER) MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

}