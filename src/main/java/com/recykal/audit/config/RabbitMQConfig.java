package com.recykal.audit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recykal.audit.constants.Constants;
import com.recykal.audit.dto.RabbitMQProperties;
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
    public Queue auditQueue(RabbitMQProperties props) {
        return new Queue(props.getQueue(), true);
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.EXCHANGE)
    public TopicExchange auditExchange(RabbitMQProperties props) {
        return new TopicExchange(props.getExchange(), true, false);
    }

    @Bean(name = Constants.RABBITMQ_CONSTANTS.BINDING)
    public Binding auditBinding(@Qualifier(Constants.RABBITMQ_CONSTANTS.QUEUE) Queue queue,
                                @Qualifier(Constants.RABBITMQ_CONSTANTS.EXCHANGE) TopicExchange exchange,
                                RabbitMQProperties props) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(props.getRoutingKey());
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