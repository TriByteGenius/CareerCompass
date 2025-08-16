package com.tribytegenius.CareerCompass.JobService.config;

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

    @Value("${rabbitmq.exchanges.job-events}")
    private String jobEventsExchange;

    @Value("${rabbitmq.exchanges.user-events}")
    private String userEventsExchange;

    @Value("${rabbitmq.queues.job-created}")
    private String jobCreatedQueue;

    @Value("${rabbitmq.queues.job-updated}")
    private String jobUpdatedQueue;

    @Value("${rabbitmq.queues.job-deleted}")
    private String jobDeletedQueue;

    @Value("${rabbitmq.queues.user-events}")
    private String userEventsQueue;

    @Value("${rabbitmq.routing-keys.job-created}")
    private String jobCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.job-updated}")
    private String jobUpdatedRoutingKey;

    @Value("${rabbitmq.routing-keys.job-deleted}")
    private String jobDeletedRoutingKey;

    // Exchanges
    @Bean
    public TopicExchange jobEventsExchange() {
        return new TopicExchange(jobEventsExchange);
    }

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    // Job Queues
    @Bean
    public Queue jobCreatedQueue() {
        return QueueBuilder.durable(jobCreatedQueue).build();
    }

    @Bean
    public Queue jobUpdatedQueue() {
        return QueueBuilder.durable(jobUpdatedQueue).build();
    }

    @Bean
    public Queue jobDeletedQueue() {
        return QueueBuilder.durable(jobDeletedQueue).build();
    }

    // User Events Queue (for listening to user events)
    @Bean
    public Queue userEventsQueue() {
        return QueueBuilder.durable(userEventsQueue).build();
    }

    // Job Event Bindings
    @Bean
    public Binding jobCreatedBinding() {
        return BindingBuilder
                .bind(jobCreatedQueue())
                .to(jobEventsExchange())
                .with(jobCreatedRoutingKey);
    }

    @Bean
    public Binding jobUpdatedBinding() {
        return BindingBuilder
                .bind(jobUpdatedQueue())
                .to(jobEventsExchange())
                .with(jobUpdatedRoutingKey);
    }

    @Bean
    public Binding jobDeletedBinding() {
        return BindingBuilder
                .bind(jobDeletedQueue())
                .to(jobEventsExchange())
                .with(jobDeletedRoutingKey);
    }

    // User Events Binding
    @Bean
    public Binding userEventsBinding() {
        return BindingBuilder
                .bind(userEventsQueue())
                .to(userEventsExchange())
                .with("user.*");
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
