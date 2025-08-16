package com.tribytegenius.CareerCompass.UserService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDTO {
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private String eventType; // CREATED, UPDATED, DELETED
    private LocalDateTime timestamp;

    public UserEventDTO(Long userId, String username, String email, List<String> roles, String eventType) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
}
