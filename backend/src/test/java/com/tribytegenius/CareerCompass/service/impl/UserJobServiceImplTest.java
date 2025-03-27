package com.tribytegenius.CareerCompass.service.impl;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.UserJobDTO;
import com.tribytegenius.CareerCompass.exception.APIException;
import com.tribytegenius.CareerCompass.exception.ResourceNotFoundException;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.model.UserJob;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import com.tribytegenius.CareerCompass.repository.UserJobRepository;
import com.tribytegenius.CareerCompass.util.AuthUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserJobServiceImplTest {

    @Mock
    private UserJobRepository userJobRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private AuthUtil authUtil;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserJobServiceImpl userJobService;

    private User testUser;
    private Job testJob;
    private UserJob testUserJob;
    private UserJobDTO testUserJobDTO;

    @BeforeEach
    public void setup() {
        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        testJob = new Job();
        testJob.setId(1L);
        testJob.setName("Software Engineer");
        testJob.setCompany("Tech Corp");
        testJob.setType("Full-time");
        testJob.setLocation("Dublin, Ireland");
        testJob.setTime(LocalDateTime.now());
        testJob.setStatus("new");
        testJob.setUrl("https://example.com/job1");
        testJob.setWebsite("LINKEDIN");

        testUserJob = new UserJob();
        testUserJob.setId(1L);
        testUserJob.setUser(testUser);
        testUserJob.setJob(testJob);
        testUserJob.setStatus("new");
        testUserJob.setStatusChangedAt(LocalDateTime.now());

        JobDTO jobDTO = modelMapper.map(testJob, JobDTO.class);

        testUserJobDTO = new UserJobDTO();
        testUserJobDTO.setId(1L);
        testUserJobDTO.setUserId(testUser.getId());
        testUserJobDTO.setUserName(testUser.getUserName());
        testUserJobDTO.setJob(jobDTO);
        testUserJobDTO.setStatus("new");
        testUserJobDTO.setStatusChangedAt(LocalDateTime.now());
    }

    @Test
    public void testToggleFavorite_AddToFavorites_ReturnsUserJobDTO() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(testJob));
        when(userJobRepository.findByUserIdAndJobId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(userJobRepository.save(any(UserJob.class))).thenReturn(testUserJob);

        // Act
        UserJobDTO result = userJobService.toggleFavorite(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testJob.getName(), result.getJob().getName());
        assertEquals("new", result.getStatus());

        verify(authUtil).loggedInUser();
        verify(jobRepository).findById(1L);
        verify(userJobRepository).findByUserIdAndJobId(1L, 1L);
        verify(userJobRepository).save(any(UserJob.class));
    }

    @Test
    public void testToggleFavorite_RemoveFromFavorites_ReturnsNull() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(testJob));
        when(userJobRepository.findByUserIdAndJobId(anyLong(), anyLong())).thenReturn(Optional.of(testUserJob));
        doNothing().when(userJobRepository).delete(any(UserJob.class));

        // Act
        UserJobDTO result = userJobService.toggleFavorite(1L);

        // Assert
        assertNull(result);

        verify(authUtil).loggedInUser();
        verify(jobRepository).findById(1L);
        verify(userJobRepository).findByUserIdAndJobId(1L, 1L);
        verify(userJobRepository).delete(testUserJob);
    }

    @Test
    public void testToggleFavorite_JobNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userJobService.toggleFavorite(1L);
        });

        verify(authUtil).loggedInUser();
        verify(jobRepository).findById(1L);
        verify(userJobRepository, never()).findByUserIdAndJobId(anyLong(), anyLong());
        verify(userJobRepository, never()).save(any(UserJob.class));
    }

    @Test
    public void testUpdateStatus_ValidStatus_ReturnsUpdatedUserJobDTO() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(userJobRepository.findByUserIdAndJobId(anyLong(), anyLong())).thenReturn(Optional.of(testUserJob));

        testUserJob.setStatus("applied");
        when(userJobRepository.save(any(UserJob.class))).thenReturn(testUserJob);

        // Act
        UserJobDTO result = userJobService.updateStatus(1L, "applied");

        // Assert
        assertNotNull(result);
        assertEquals("applied", result.getStatus());

        verify(authUtil).loggedInUser();
        verify(userJobRepository).findByUserIdAndJobId(1L, 1L);
        verify(userJobRepository).save(testUserJob);
    }

    @Test
    public void testUpdateStatus_InvalidStatus_ThrowsAPIException() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(userJobRepository.findByUserIdAndJobId(anyLong(), anyLong())).thenReturn(Optional.of(testUserJob));

        // Act & Assert
        assertThrows(APIException.class, () -> {
            userJobService.updateStatus(1L, "invalid_status");
        });

        verify(authUtil).loggedInUser();
        verify(userJobRepository).findByUserIdAndJobId(1L, 1L);
        verify(userJobRepository, never()).save(any(UserJob.class));
    }

    @Test
    public void testUpdateStatus_UserJobNotFound_ThrowsAPIException() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(userJobRepository.findByUserIdAndJobId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(APIException.class, () -> {
            userJobService.updateStatus(1L, "applied");
        });

        verify(authUtil).loggedInUser();
        verify(userJobRepository).findByUserIdAndJobId(1L, 1L);
        verify(userJobRepository, never()).save(any(UserJob.class));
    }

    @Test
    public void testGetFavoriteJobs_ReturnsListOfUserJobDTOs() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(userJobRepository.findByUserId(anyLong())).thenReturn(Arrays.asList(testUserJob));

        // Act
        List<UserJobDTO> result = userJobService.getFavoriteJobs();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testJob.getName(), result.get(0).getJob().getName());

        verify(authUtil).loggedInUser();
        verify(userJobRepository).findByUserId(1L);
    }

    @Test
    public void testGetJobsByStatus_ReturnsFilteredListOfUserJobDTOs() {
        // Arrange
        when(authUtil.loggedInUser()).thenReturn(testUser);
        when(userJobRepository.findByUserIdAndStatus(anyLong(), anyString())).thenReturn(Arrays.asList(testUserJob));

        // Act
        List<UserJobDTO> result = userJobService.getJobsByStatus("new");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("new", result.get(0).getStatus());

        verify(authUtil).loggedInUser();
        verify(userJobRepository).findByUserIdAndStatus(1L, "new");
    }

    @Test
    public void testGetJobsByStatus_InvalidStatus_ThrowsAPIException() {
        // Act & Assert
        assertThrows(APIException.class, () -> {
            userJobService.getJobsByStatus("invalid_status");
        });

        verify(userJobRepository, never()).findByUserIdAndStatus(anyLong(), anyString());
    }
}