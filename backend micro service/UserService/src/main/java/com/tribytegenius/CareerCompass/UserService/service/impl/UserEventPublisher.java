package com.tribytegenius.CareerCompass.UserService.service.impl;

import com.tribytegenius.CareerCompass.UserService.dto.UserEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.user-events}")
    private String userEventsExchange;

    @Value("${rabbitmq.routing-keys.user-created}")
    private String userCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.user-updated}")
    private String userUpdatedRoutingKey;

    @Value("${rabbitmq.routing-keys.user-deleted}")
    private String userDeletedRoutingKey;

    public void publishUserCreated(UserEventDTO userEvent) {
        try {
            logger.info("Publishing user created event for user ID: {}", userEvent.getUserId());
            rabbitTemplate.convertAndSend(userEventsExchange, userCreatedRoutingKey, userEvent);
            logger.info("Successfully published user created event");
        } catch (Exception e) {
            logger.error("Failed to publish user created event: {}", e.getMessage(), e);
        }
    }

    public void publishUserUpdated(UserEventDTO userEvent) {
        try {
            logger.info("Publishing user updated event for user ID: {}", userEvent.getUserId());
            rabbitTemplate.convertAndSend(userEventsExchange, userUpdatedRoutingKey, userEvent);
            logger.info("Successfully published user updated event");
        } catch (Exception e) {
            logger.error("Failed to publish user updated event: {}", e.getMessage(), e);
        }
    }

    public void publishUserDeleted(UserEventDTO userEvent) {
        try {
            logger.info("Publishing user deleted event for user ID: {}", userEvent.getUserId());
            rabbitTemplate.convertAndSend(userEventsExchange, userDeletedRoutingKey, userEvent);
            logger.info("Successfully published user deleted event");
        } catch (Exception e) {
            logger.error("Failed to publish user deleted event: {}", e.getMessage(), e);
        }
    }
}
