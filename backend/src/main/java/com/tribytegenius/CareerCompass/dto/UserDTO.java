package com.tribytegenius.CareerCompass.dto;

import com.tribytegenius.CareerCompass.model.Job;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<Job> favorites;
}
