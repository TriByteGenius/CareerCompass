package com.tribytegenius.CareerCompass.UserService.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String jwtToken;
    private String username;
    private String email;
    private List<String> roles;
}
