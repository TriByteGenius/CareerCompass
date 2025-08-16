package com.tribytegenius.CareerCompass.UserJobService.service.impl;

import com.tribytegenius.CareerCompass.UserJobService.model.Job;
import com.tribytegenius.CareerCompass.UserJobService.model.User;
import com.tribytegenius.CareerCompass.UserJobService.repository.JobRepository;
import com.tribytegenius.CareerCompass.UserJobService.repository.UserJobRepository;
import com.tribytegenius.CareerCompass.UserJobService.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class EventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserJobRepository userJobRepository;

    @RabbitListener(queues = "${rabbitmq.queues.user-events}")
    @Transactional
    public void handleUserEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            Long userId = Long.valueOf(event.get("userId").toString());

            logger.info("Received user event: {} for user ID: {}", eventType, userId);

            switch (eventType) {
                case "CREATED":
                case "UPDATED":
                    User user = new User();
                    user.setId(userId);
                    user.setUserName((String) event.get("username"));
                    user.setEmail((String) event.get("email"));
                    user.setRoles((java.util.List<String>) event.get("roles"));
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    logger.info("User cache updated for user ID: {}", userId);
                    break;

                case "DELETED":
                    userRepository.deleteById(userId);
                    userJobRepository.deleteByUserId(userId);
                    logger.info("User cache and related UserJobs deleted for user ID: {}", userId);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing user event: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.job-events}")
    @Transactional
    public void handleJobEvent(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            Long jobId = Long.valueOf(event.get("jobId").toString());

            logger.info("Received job event: {} for job ID: {}", eventType, jobId);

            switch (eventType) {
                case "CREATED":
                case "UPDATED":
                    Job job = new Job();
                    job.setId(jobId);
                    job.setName((String) event.get("name"));
                    job.setCompany((String) event.get("company"));
                    job.setType((String) event.get("type"));
                    job.setLocation((String) event.get("location"));
                    job.setWebsite((String) event.get("website"));
                    job.setUrl((String) event.get("url"));
                    job.setStatus("new");
                    job.setTime(LocalDateTime.now());
                    jobRepository.save(job);
                    logger.info("Job cache updated for job ID: {}", jobId);
                    break;

                case "DELETED":
                    jobRepository.deleteById(jobId);
                    userJobRepository.deleteByJobId(jobId);
                    logger.info("Job cache and related UserJobs deleted for job ID: {}", jobId);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error processing job event: {}", e.getMessage(), e);
        }
    }
}