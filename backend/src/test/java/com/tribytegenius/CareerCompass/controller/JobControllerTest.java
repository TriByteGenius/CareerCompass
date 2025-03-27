package com.tribytegenius.CareerCompass.controller;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.JobResponse;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.service.JobService;
import com.tribytegenius.CareerCompass.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private JobController jobController;

    private JobDTO jobDTO1;
    private JobDTO jobDTO2;
    private JobResponse jobResponse;

    @BeforeEach
    public void setup() {
        // Set up test data
        jobDTO1 = new JobDTO();
        jobDTO1.setId("1");
        jobDTO1.setName("Software Engineer");
        jobDTO1.setCompany("Tech Corp");
        jobDTO1.setType("Full-time");
        jobDTO1.setLocation("Dublin, Ireland");
        jobDTO1.setTime(LocalDateTime.now());
        jobDTO1.setStatus("new");
        jobDTO1.setUrl("https://example.com/job1");
        jobDTO1.setWebsite("LINKEDIN");

        jobDTO2 = new JobDTO();
        jobDTO2.setId("2");
        jobDTO2.setName("Backend Developer");
        jobDTO2.setCompany("Startup Inc");
        jobDTO2.setType("Contract");
        jobDTO2.setLocation("Cork, Ireland");
        jobDTO2.setTime(LocalDateTime.now().minusDays(7));
        jobDTO2.setStatus("new");
        jobDTO2.setUrl("https://example.com/job2");
        jobDTO2.setWebsite("INDEED");

        List<JobDTO> jobs = new ArrayList<>(Arrays.asList(jobDTO1, jobDTO2));

        jobResponse = new JobResponse();
        jobResponse.setContent(jobs);
        jobResponse.setPageNumber(0);
        jobResponse.setPageSize(10);
        jobResponse.setTotalElements(2L);
        jobResponse.setTotalPages(1);
        jobResponse.setLastPage(true);
    }

    @Test
    public void testGetAllJobs_ReturnsSuccessResponse() {
        // Arrange
        when(jobService.getAllJobs(
                anyInt(), anyInt(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyInt()
        )).thenReturn(jobResponse);

        // Act
        ResponseEntity<JobResponse> response = jobController.getAllJobs(
                "software", "new", "LINKEDIN", 30, 0, 10, "time", "desc");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());

        verify(jobService).getAllJobs(0, 10, "time", "desc", "software", "new", "LINKEDIN", 30);
    }

    @Test
    public void testCreateJob_ReturnsCreatedResponse() {
        // Arrange
        when(jobService.createJob(any(JobDTO.class))).thenReturn(jobDTO1);

        // Act
        ResponseEntity<JobDTO> response = jobController.createJob(jobDTO1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Software Engineer", response.getBody().getName());

        verify(jobService).createJob(jobDTO1);
    }

    @Test
    public void testSearchJob_CallsServiceAndReturnsVoid() {
        // Arrange
        SearchRequestBody searchRequest = new SearchRequestBody();
        searchRequest.setWebsite("LINKEDIN");
        searchRequest.setType(Arrays.asList("Software Engineer", "Developer"));
        searchRequest.setLocation("Ireland");
        searchRequest.setTime(7);

        doNothing().when(jobService).searchJob(any(SearchRequestBody.class));

        // Act
        jobController.searchJob(searchRequest);

        // Assert
        verify(jobService).searchJob(searchRequest);
    }

    @Test
    public void testUpdateJob_ReturnsUpdatedJob() {
        // Arrange
        when(jobService.updateJob(anyLong(), any(JobDTO.class))).thenReturn(jobDTO1);

        // Act
        ResponseEntity<JobDTO> response = jobController.updateJob(1L, jobDTO1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Software Engineer", response.getBody().getName());

        verify(jobService).updateJob(1L, jobDTO1);
    }

    @Test
    public void testDeleteJob_ReturnsSuccessMessage() {
        // Arrange
        when(jobService.deleteJob(anyLong())).thenReturn("Job deleted");

        // Act
        ResponseEntity<String> response = jobController.deleteJob(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Job deleted", response.getBody());

        verify(jobService).deleteJob(1L);
    }
}