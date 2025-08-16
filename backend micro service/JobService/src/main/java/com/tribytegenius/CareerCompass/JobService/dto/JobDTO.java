package com.tribytegenius.CareerCompass.JobService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private String id;
    private String name;
    private String company;
    private String type;
    private String location;
    private LocalDateTime time;
    private String status;
    private String url;
    private String website;
}
