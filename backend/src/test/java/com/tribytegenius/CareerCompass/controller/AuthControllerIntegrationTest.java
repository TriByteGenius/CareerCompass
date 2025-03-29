package com.tribytegenius.CareerCompass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tribytegenius.CareerCompass.model.AppRole;
import com.tribytegenius.CareerCompass.model.Role;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.repository.RoleRepository;
import com.tribytegenius.CareerCompass.repository.UserRepository;
import com.tribytegenius.CareerCompass.security.dto.LoginRequest;
import com.tribytegenius.CareerCompass.security.dto.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Clean up user repository
        userRepository.deleteAll();

        // Ensure roles exist
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

        // Create test user
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        testUser.setRoles(roles);
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testSignup_WithValidData_ReturnsSuccessMessage() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("newuser@example.com");
        signupRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully")));
    }

    @Test
    public void testSignup_WithExistingUsername_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser"); // Already exists
        signupRequest.setEmail("another@example.com");
        signupRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Username is already taken!")));
    }

    @Test
    public void testSignup_WithExistingEmail_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("anotherusername");
        signupRequest.setEmail("test@example.com"); // Already exists
        signupRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: Email is already in use!")));
    }

    @Test
    public void testSignin_WithValidCredentials_ReturnsJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken", not(emptyString())))
                .andExpect(jsonPath("$.username", is("test@example.com")));
    }

    @Test
    public void testSignin_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Bad credentials")));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testGetCurrentUser_ReturnsUserDetails() throws Exception {
        mockMvc.perform(get("/api/auth/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    public void testSignout_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("You've been logged out successfully")));
    }
}