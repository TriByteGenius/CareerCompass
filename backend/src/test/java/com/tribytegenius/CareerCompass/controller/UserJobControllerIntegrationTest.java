package com.tribytegenius.CareerCompass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tribytegenius.CareerCompass.model.AppRole;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.model.Role;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.model.UserJob;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import com.tribytegenius.CareerCompass.repository.RoleRepository;
import com.tribytegenius.CareerCompass.repository.UserJobRepository;
import com.tribytegenius.CareerCompass.repository.UserRepository;
import com.tribytegenius.CareerCompass.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserJobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserJobRepository userJobRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Job testJob;
    private UserJob testUserJob;

    @BeforeEach
    public void setup() {
        // Clear repositories
        userJobRepository.deleteAll();
        jobRepository.deleteAll();
        userRepository.deleteAll();

        // Create role if not exists
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

        // Create test job
        testJob = new Job();
        testJob.setName("Software Engineer");
        testJob.setCompany("Test Company");
        testJob.setType("Full-time");
        testJob.setLocation("Dublin, Ireland");
        testJob.setTime(LocalDateTime.now());
        testJob.setStatus("new");
        testJob.setUrl("https://example.com/job1");
        testJob.setWebsite("LINKEDIN");
        testJob = jobRepository.save(testJob);

        // Set up authentication context for tests
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(AppRole.ROLE_USER.name())
        );

        UserDetailsImpl userDetails = new UserDetailsImpl(
                testUser.getId(),
                testUser.getUserName(),
                testUser.getEmail(),
                testUser.getPassword(),
                authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create test user job (favorite) for some tests
        testUserJob = new UserJob();
        testUserJob.setUser(testUser);
        testUserJob.setJob(testJob);
        testUserJob.setStatus("new");
        testUserJob.setStatusChangedAt(LocalDateTime.now());
        testUserJob = userJobRepository.save(testUserJob);
    }

    @Test
    public void testToggleFavorite_AddNewJobToFavorites_ReturnsCreatedStatusWithUserJobDetails() throws Exception {
        // First delete the existing favorite to test adding
        userJobRepository.deleteAll();

        mockMvc.perform(post("/api/favorites/{jobId}/toggle", testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.job.name", is("Software Engineer")))
                .andExpect(jsonPath("$.status", is("new")));
    }

    @Test
    public void testToggleFavorite_RemoveExistingFavorite_ReturnsOkWithMessage() throws Exception {
        mockMvc.perform(post("/api/favorites/{jobId}/toggle", testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Job removed from favorites")));
    }

    @Test
    public void testUpdateStatus_WithValidStatus_ReturnsUpdatedUserJobDTO() throws Exception {
        mockMvc.perform(put("/api/favorites/{jobId}/status", testJob.getId())
                        .param("status", "applied")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("applied")))
                .andExpect(jsonPath("$.job.name", is("Software Engineer")));
    }

    @Test
    public void testUpdateStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/favorites/{jobId}/status", testJob.getId())
                        .param("status", "invalid_status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetFavoriteJobs_ReturnsListOfUserJobDTOs() throws Exception {
        mockMvc.perform(get("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].job.name", is("Software Engineer")))
                .andExpect(jsonPath("$[0].status", is("new")));
    }

    @Test
    public void testGetJobsByStatus_WithValidStatus_ReturnsFilteredUserJobDTOs() throws Exception {
        mockMvc.perform(get("/api/favorites/status/{status}", "new")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("new")));
    }

    @Test
    public void testGetJobsByStatus_WithInvalidStatus_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/favorites/status/{status}", "invalid_status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}