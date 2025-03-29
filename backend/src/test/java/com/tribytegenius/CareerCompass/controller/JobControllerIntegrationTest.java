package com.tribytegenius.CareerCompass.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import com.tribytegenius.CareerCompass.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    private Job testJob;

    @BeforeEach
    public void setup() {
        // Clear any existing data
        jobRepository.deleteAll();

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

        jobRepository.save(testJob);
    }

    @Test
    public void testGetAllJobs_ReturnsOkAndJobList() throws Exception {
        mockMvc.perform(get("/api/jobs")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].name", is("Software Engineer")))
                .andExpect(jsonPath("$.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageSize", is(10)));
    }

    @Test
    public void testGetAllJobs_WithFilters_ReturnsFilteredJobs() throws Exception {
        mockMvc.perform(get("/api/jobs")
                        .param("keyword", "Software")
                        .param("website", "LINKEDIN")
                        .param("pageNumber", "0")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].name", is("Software Engineer")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateJob_ReturnsCreatedJobDetails() throws Exception {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setName("Backend Developer");
        jobDTO.setCompany("New Company");
        jobDTO.setType("Contract");
        jobDTO.setLocation("Cork, Ireland");
        jobDTO.setStatus("new");
        jobDTO.setUrl("https://example.com/job2");
        jobDTO.setWebsite("INDEED");

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Backend Developer")))
                .andExpect(jsonPath("$.company", is("New Company")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateJob_ReturnsUpdatedJobDetails() throws Exception {
        JobDTO updateDTO = new JobDTO();
        updateDTO.setName("Updated Job Title");
        updateDTO.setCompany("Updated Company");
        updateDTO.setType("Part-time");
        updateDTO.setLocation("Galway, Ireland");
        updateDTO.setStatus("applied");
        updateDTO.setUrl("https://example.com/updated");
        updateDTO.setWebsite("JOBS");

        mockMvc.perform(put("/api/jobs/{id}", testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Job Title")))
                .andExpect(jsonPath("$.company", is("Updated Company")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteJob_ReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/jobs/{id}", testJob.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Job deleted"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testSearchJob_WithValidData_ReturnsSuccessful() throws Exception {
        SearchRequestBody searchRequest = new SearchRequestBody();
        searchRequest.setWebsite("LINKEDIN");
        searchRequest.setType(Arrays.asList("Software Engineer", "Developer"));
        searchRequest.setLocation("Ireland");
        searchRequest.setTime(7);

        mockMvc.perform(post("/api/jobs/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk());
    }
}