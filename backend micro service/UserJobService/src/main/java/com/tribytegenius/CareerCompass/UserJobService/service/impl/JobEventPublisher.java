package com.tribytegenius.CareerCompass.UserJobService.service.impl;

import com.tribytegenius.CareerCompass.UserJobService.dto.JobEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JobEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(JobEventPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.job-events}")
    private String jobEventsExchange;

    @Value("${rabbitmq.routing-keys.job-created}")
    private String jobCreatedRoutingKey;

    @Value("${rabbitmq.routing-keys.job-updated}")
    private String jobUpdatedRoutingKey;

    @Value("${rabbitmq.routing-keys.job-deleted}")
    private String jobDeletedRoutingKey;

    public void publishJobCreated(JobEventDTO jobEvent) {
        try {
            logger.info("Publishing job created event for job ID: {}", jobEvent.getJobId());
            rabbitTemplate.convertAndSend(jobEventsExchange, jobCreatedRoutingKey, jobEvent);
            logger.info("Successfully published job created event");
        } catch (Exception e) {
            logger.error("Failed to publish job created event: {}", e.getMessage(), e);
        }
    }

    public void publishJobUpdated(JobEventDTO jobEvent) {
        try {
            logger.info("Publishing job updated event for job ID: {}", jobEvent.getJobId());
            rabbitTemplate.convertAndSend(jobEventsExchange, jobUpdatedRoutingKey, jobEvent);
            logger.info("Successfully published job updated event");
        } catch (Exception e) {
            logger.error("Failed to publish job updated event: {}", e.getMessage(), e);
        }
    }

    public void publishJobDeleted(JobEventDTO jobEvent) {
        try {
            logger.info("Publishing job deleted event for job ID: {}", jobEvent.getJobId());
            rabbitTemplate.convertAndSend(jobEventsExchange, jobDeletedRoutingKey, jobEvent);
            logger.info("Successfully published job deleted event");
        } catch (Exception e) {
            logger.error("Failed to publish job deleted event: {}", e.getMessage(), e);
        }
    }
}