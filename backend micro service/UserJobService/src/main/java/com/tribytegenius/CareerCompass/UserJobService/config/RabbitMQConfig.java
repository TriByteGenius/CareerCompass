package com.tribytegenius.CareerCompass.UserJobService.config;

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

    @Value("${rabbitmq.exchanges.userjob-events}")
    private String userJobEventsExchange;

    @Value("${rabbitmq.exchanges.user-events}")
    private String userEventsExchange;

    @Value("${rabbitmq.exchanges.job-events}")
    private String jobEventsExchange;

    @Value("${rabbitmq.queues.user-events}")
    private String userEventsQueue;

    @Value("${rabbitmq.queues.job-events}")
    private String jobEventsQueue;

    // Exchanges
    @Bean
    public TopicExchange userJobEventsExchange() {
        return new TopicExchange(userJobEventsExchange);
    }

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(userEventsExchange);
    }

    @Bean
    public TopicExchange jobEventsExchange() {
        return new TopicExchange(jobEventsExchange);
    }

    // Queues for listening to external events
    @Bean
    public Queue userEventsQueue() {
        return QueueBuilder.durable(userEventsQueue).build();
    }

    @Bean
    public Queue jobEventsQueue() {
        return QueueBuilder.durable(jobEventsQueue).build();
    }

    // Bindings for external events
    @Bean
    public Binding userEventsBinding() {
        return BindingBuilder
                .bind(userEventsQueue())
                .to(userEventsExchange())
                .with("user.*");
    }

    @Bean
    public Binding jobEventsBinding() {
        return BindingBuilder
                .bind(jobEventsQueue())
                .to(jobEventsExchange())
                .with("job.*");
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
