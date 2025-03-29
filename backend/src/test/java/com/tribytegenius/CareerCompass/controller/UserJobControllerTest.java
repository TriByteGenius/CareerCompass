package com.tribytegenius.CareerCompass.controller;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.UserJobDTO;
import com.tribytegenius.CareerCompass.security.dto.MessageResponse;
import com.tribytegenius.CareerCompass.service.UserJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserJobControllerTest {

    @Mock
    private UserJobService userJobService;

    @InjectMocks
    private UserJobController userJobController;

    private UserJobDTO testUserJobDTO;
    private JobDTO testJobDTO;

    @BeforeEach
    public void setup() {
        // Setup test data
        testJobDTO = new JobDTO();
        testJobDTO.setId("1");
        testJobDTO.setName("Software Engineer");
        testJobDTO.setCompany("Tech Corp");
        testJobDTO.setType("Full-time");
        testJobDTO.setLocation("Dublin, Ireland");
        testJobDTO.setTime(LocalDateTime.now());
        testJobDTO.setStatus("new");
        testJobDTO.setUrl("https://example.com/job1");
        testJobDTO.setWebsite("LINKEDIN");

        testUserJobDTO = new UserJobDTO();
        testUserJobDTO.setId(1L);
        testUserJobDTO.setUserId(1L);
        testUserJobDTO.setUserName("testuser");
        testUserJobDTO.setJob(testJobDTO);
        testUserJobDTO.setStatus("new");
        testUserJobDTO.setStatusChangedAt(LocalDateTime.now());
    }

    @Test
    public void testToggleFavorite_AddNewFavorite_ReturnsCreatedStatusWithUserJobDTO() {
        // Arrange
        when(userJobService.toggleFavorite(anyLong())).thenReturn(testUserJobDTO);

        // Act
        ResponseEntity<?> response = userJobController.toggleFavorite(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserJobDTO);
        assertEquals("Software Engineer", ((UserJobDTO) response.getBody()).getJob().getName());

        verify(userJobService).toggleFavorite(1L);
    }

    @Test
    public void testToggleFavorite_RemoveFavorite_ReturnsOkStatusWithMessage() {
        // Arrange
        when(userJobService.toggleFavorite(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<?> response = userJobController.toggleFavorite(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals("Job removed from favorites", ((MessageResponse) response.getBody()).getMessage());

        verify(userJobService).toggleFavorite(1L);
    }

    @Test
    public void testUpdateStatus_ReturnsUpdatedUserJobDTO() {
        // Arrange
        when(userJobService.updateStatus(anyLong(), anyString())).thenReturn(testUserJobDTO);

        // Act
        ResponseEntity<UserJobDTO> response = userJobController.updateStatus(1L, "applied");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserJobDTO, response.getBody());

        verify(userJobService).updateStatus(1L, "applied");
    }

    @Test
    public void testGetFavoriteJobs_ReturnsListOfUserJobDTOs() {
        // Arrange
        List<UserJobDTO> favoriteJobs = Arrays.asList(testUserJobDTO);
        when(userJobService.getFavoriteJobs()).thenReturn(favoriteJobs);

        // Act
        ResponseEntity<List<UserJobDTO>> response = userJobController.getFavoriteJobs();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Software Engineer", response.getBody().get(0).getJob().getName());

        verify(userJobService).getFavoriteJobs();
    }

    @Test
    public void testGetJobsByStatus_ReturnsFilteredListOfUserJobDTOs() {
        // Arrange
        List<UserJobDTO> statusJobs = Arrays.asList(testUserJobDTO);
        when(userJobService.getJobsByStatus(anyString())).thenReturn(statusJobs);

        // Act
        ResponseEntity<List<UserJobDTO>> response = userJobController.getJobsByStatus("new");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("new", response.getBody().get(0).getStatus());

        verify(userJobService).getJobsByStatus("new");
    }
}