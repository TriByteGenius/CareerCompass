package com.tribytegenius.CareerCompass.JobService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobEventDTO {
    private Long jobId;
    private String name;
    private String company;
    private String type;
    private String location;
    private String website;
    private String url;
    private String eventType; // CREATED, UPDATED, DELETED
    private LocalDateTime timestamp;

    public JobEventDTO(Long jobId, String name, String company, String type,
                       String location, String website, String url, String eventType) {
        this.jobId = jobId;
        this.name = name;
        this.company = company;
        this.type = type;
        this.location = location;
        this.website = website;
        this.url = url;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
}
