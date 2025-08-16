package com.tribytegenius.CareerCompass.UserService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchanges.user-events}")
    private String userEventsExchange;

    @Value("${rabbitmq.queues.user-created}")
    private String userCreatedQueue;

    @Value("${rabbitmq.queues.user-updated}")
    private String userUpdatedQueue;

    @Value("${rabbitmq.queues.user-deleted}")
    private String userDeletedQueue;

    @Value("${rabbitmq.routing-keys.user-created}")
    private String userCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.user-updated}")
    private String userUpdatedRoutingKey;

    @Value("${rabbitmq.routing-keys.user-deleted}")
    private String userDeletedRoutingKey;

    // Exchange
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    // Queues
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(userCreatedQueue).build();
    }

    @Bean
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(userUpdatedQueue).build();
    }

    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(userDeletedQueue).build();
    }

    // Bindings
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder
                .bind(userCreatedQueue())
                .to(userEventsExchange())
                .with(userCreatedRoutingKey);
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(userUpdatedRoutingKey);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(userEventsExchange())
                .with(userDeletedRoutingKey);
    }

    // Message converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
