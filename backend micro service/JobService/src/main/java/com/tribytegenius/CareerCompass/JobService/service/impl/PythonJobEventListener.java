package com.tribytegenius.CareerCompass.JobService.service.impl;

import com.tribytegenius.CareerCompass.JobService.model.Job;
import com.tribytegenius.CareerCompass.JobService.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class PythonJobEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PythonJobEventListener.class);

    @Autowired
    private JobRepository jobRepository;

    @RabbitListener(queues = "${rabbitmq.queues.job-created}")
    @Transactional
    public void handlePythonJobCreated(Map<String, Object> event) {
        try {
            String eventType = (String) event.get("eventType");
            
            if ("CREATED".equals(eventType)) {
                // Extract job data from event
                String name = (String) event.get("name");
                String company = (String) event.get("company");
                String type = (String) event.get("type");
                String location = (String) event.get("location");
                String website = (String) event.get("website");
                String url = (String) event.get("url");
                String status = (String) event.get("status");
                String timeStr = (String) event.get("time");
                
                // Parse time
                LocalDateTime time = LocalDateTime.now();
                if (timeStr != null && !timeStr.isEmpty()) {
                    try {
                        // Handle different time formats
                        if (timeStr.contains("T")) {
                            time = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        } else {
                            time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to parse time: {}, using current time", timeStr);
                        time = LocalDateTime.now();
                    }
                }
                
                // Check if job already exists by URL
                if (jobRepository.findByUrl(url).isPresent()) {
                    logger.info("Job already exists with URL: {}", url);
                    return;
                }
                
                // Create new job
                Job job = new Job();
                job.setName(name);
                job.setCompany(company);
                job.setType(type);
                job.setLocation(location);
                job.setWebsite(website);
                job.setUrl(url);
                job.setStatus(status);
                job.setTime(time);
                
                Job savedJob = jobRepository.save(job);
                logger.info("Created job from Python Service: {} at {}", savedJob.getName(), savedJob.getCompany());
                
            } else {
                logger.warn("Received unknown event type: {}", eventType);
            }
            
        } catch (Exception e) {
            logger.error("Error processing Python job event: {}", e.getMessage(), e);
        }
    }
}
