package com.tribytegenius.CareerCompass.UserJobService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJobDTO {
    private Long id;
    private Long userId;
    private String userName;
    private JobDTO job;
    private String status;
    private LocalDateTime statusChangedAt;
}