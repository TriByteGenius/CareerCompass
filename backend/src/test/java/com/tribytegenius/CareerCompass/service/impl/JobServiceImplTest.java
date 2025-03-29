package com.tribytegenius.CareerCompass.service.impl;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.JobResponse;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.exception.ResourceNotFoundException;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private JobServiceImpl jobService;

    private Job job1;
    private Job job2;
    private JobDTO jobDTO1;
    private JobDTO jobDTO2;

    @BeforeEach
    public void setup() {
        // Setup test data
        job1 = new Job();
        job1.setId(1L);
        job1.setName("Software Engineer");
        job1.setCompany("Tech Corp");
        job1.setType("Full-time");
        job1.setLocation("Dublin, Ireland");
        job1.setTime(LocalDateTime.now());
        job1.setStatus("new");
        job1.setUrl("https://example.com/job1");
        job1.setWebsite("LINKEDIN");

        job2 = new Job();
        job2.setId(2L);
        job2.setName("Backend Developer");
        job2.setCompany("Startup Inc");
        job2.setType("Contract");
        job2.setLocation("Cork, Ireland");
        job2.setTime(LocalDateTime.now().minusDays(7));
        job2.setStatus("new");
        job2.setUrl("https://example.com/job2");
        job2.setWebsite("INDEED");

        jobDTO1 = modelMapper.map(job1, JobDTO.class);
        jobDTO2 = modelMapper.map(job2, JobDTO.class);
    }

    @Test
    public void testGetAllJobs_ReturnsCorrectResponse() {
        // Arrange
        List<Job> jobs = Arrays.asList(job1, job2);
        Page<Job> jobPage = new PageImpl<>(jobs);

        when(jobRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(jobPage);

        // Act
        JobResponse response = jobService.getAllJobs(
                0, 10, "time", "desc",
                "engineer", "new", "LINKEDIN", 30);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(2, response.getPageSize());
        assertEquals(2, response.getTotalElements());

        verify(jobRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testCreateJob_ReturnsCreatedJobDTO() {
        // Arrange
        when(jobRepository.save(any(Job.class))).thenReturn(job1);

        // Act
        JobDTO result = jobService.createJob(jobDTO1);

        // Assert
        assertNotNull(result);
        assertEquals("Software Engineer", result.getName());
        assertEquals("Tech Corp", result.getCompany());

        verify(jobRepository).save(any(Job.class));
    }

    @Test
    public void testUpdateJob_WithValidId_ReturnsUpdatedJobDTO() {
        // Arrange
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job1));
        when(jobRepository.save(any(Job.class))).thenReturn(job1);

        JobDTO updateDTO = new JobDTO();
        updateDTO.setName("Updated Job Title");
        updateDTO.setCompany("Updated Company");
        updateDTO.setType("Part-time");
        updateDTO.setLocation("Galway, Ireland");
        updateDTO.setStatus("applied");
        updateDTO.setUrl("https://example.com/updated");
        updateDTO.setWebsite("JOBS");

        // Act
        JobDTO result = jobService.updateJob(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Job Title", result.getName());
        assertEquals("Updated Company", result.getCompany());
        assertEquals("Part-time", result.getType());

        verify(jobRepository).findById(1L);
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    public void testUpdateJob_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        JobDTO updateDTO = new JobDTO();
        updateDTO.setName("Updated Job Title");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.updateJob(999L, updateDTO);
        });

        verify(jobRepository).findById(999L);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    public void testDeleteJob_WithValidId_ReturnsSuccessMessage() {
        // Arrange
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job1));
        doNothing().when(jobRepository).delete(any(Job.class));

        // Act
        String result = jobService.deleteJob(1L);

        // Assert
        assertEquals("Job deleted", result);

        verify(jobRepository).findById(1L);
        verify(jobRepository).delete(job1);
    }

    @Test
    public void testDeleteJob_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            jobService.deleteJob(999L);
        });

        verify(jobRepository).findById(999L);
        verify(jobRepository, never()).delete(any(Job.class));
    }

    @Test
    public void testSearchJob_CallsPythonService() {
        // Arrange
        SearchRequestBody searchRequest = new SearchRequestBody();
        searchRequest.setWebsite("LINKEDIN");
        searchRequest.setType(Arrays.asList("Software Engineer", "Developer"));
        searchRequest.setLocation("Ireland");
        searchRequest.setTime(7);

        // Setup WebClient mock chain with proper return types
        doReturn(requestBodyUriSpec).when(webClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).bodyValue(any(SearchRequestBody.class));
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(Mono.empty()).when(responseSpec).bodyToMono(eq(Void.class));

        // Act
        jobService.searchJob(searchRequest);

        // Assert
        verify(webClient).post();
        verify(requestBodyUriSpec).bodyValue(searchRequest);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).bodyToMono(Void.class);
    }
}