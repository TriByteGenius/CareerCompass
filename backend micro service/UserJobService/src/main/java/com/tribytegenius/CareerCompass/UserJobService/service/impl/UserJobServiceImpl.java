package com.tribytegenius.CareerCompass.UserJobService.service.impl;

import com.tribytegenius.CareerCompass.UserJobService.dto.JobDTO;
import com.tribytegenius.CareerCompass.UserJobService.dto.UserJobDTO;
import com.tribytegenius.CareerCompass.UserJobService.exception.APIException;
import com.tribytegenius.CareerCompass.UserJobService.exception.ResourceNotFoundException;
import com.tribytegenius.CareerCompass.UserJobService.model.Job;
import com.tribytegenius.CareerCompass.UserJobService.model.User;
import com.tribytegenius.CareerCompass.UserJobService.model.UserJob;
import com.tribytegenius.CareerCompass.UserJobService.repository.JobRepository;
import com.tribytegenius.CareerCompass.UserJobService.repository.UserJobRepository;
import com.tribytegenius.CareerCompass.UserJobService.repository.UserRepository;
import com.tribytegenius.CareerCompass.UserJobService.service.UserJobService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserJobServiceImpl implements UserJobService {

    private static final Logger logger = LoggerFactory.getLogger(UserJobServiceImpl.class);

    @Autowired
    private UserJobRepository userJobRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserJobDTO toggleFavorite(Long userId, Long jobId) {
        // Check if user exists in cache
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if job exists in cache
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        Optional<UserJob> existingUserJob = userJobRepository.findByUserIdAndJobId(userId, jobId);

        if (existingUserJob.isPresent()) {
            // If it exists, remove it (unfavorite)
            userJobRepository.delete(existingUserJob.get());
            logger.info("User {} unfavorited job {}", userId, jobId);
            return null;
        } else {
            // If it doesn't exist, create it (favorite)
            UserJob userJob = new UserJob();
            userJob.setUserId(userId);
            userJob.setJobId(jobId);
            userJob.setStatus("new"); // Default status for new favorites
            userJob.setStatusChangedAt(LocalDateTime.now());

            UserJob savedUserJob = userJobRepository.save(userJob);
            logger.info("User {} favorited job {}", userId, jobId);
            return convertToDTO(savedUserJob, user, job);
        }
    }

    @Override
    public UserJobDTO updateStatus(Long userId, Long jobId, String status) {
        // Validate status
        validateStatus(status);

        UserJob userJob = userJobRepository.findByUserIdAndJobId(userId, jobId)
                .orElseThrow(() -> new APIException("Job not found in user's favorites"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        userJob.setStatus(status);
        userJob.setStatusChangedAt(LocalDateTime.now());

        UserJob updatedUserJob = userJobRepository.save(userJob);
        logger.info("Updated job {} status to {} for user {}", jobId, status, userId);
        return convertToDTO(updatedUserJob, user, job);
    }

    @Override
    public List<UserJobDTO> getFavoriteJobs(Long userId) {
        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<UserJob> favoriteJobs = userJobRepository.findByUserId(userId);

        return favoriteJobs.stream()
                .map(userJob -> {
                    User user = userRepository.findById(userJob.getUserId()).orElse(null);
                    Job job = jobRepository.findById(userJob.getJobId()).orElse(null);
                    return convertToDTO(userJob, user, job);
                })
                .toList();
    }

    @Override
    public List<UserJobDTO> getJobsByStatus(Long userId, String status) {
        // Validate status
        validateStatus(status);

        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<UserJob> jobsWithStatus = userJobRepository.findByUserIdAndStatus(userId, status);

        return jobsWithStatus.stream()
                .map(userJob -> {
                    User user = userRepository.findById(userJob.getUserId()).orElse(null);
                    Job job = jobRepository.findById(userJob.getJobId()).orElse(null);
                    return convertToDTO(userJob, user, job);
                })
                .toList();
    }

    // Helper method to convert entity to DTO
    private UserJobDTO convertToDTO(UserJob userJob, User user, Job job) {
        if (userJob == null) {
            return null;
        }

        UserJobDTO userJobDTO = new UserJobDTO();
        userJobDTO.setId(userJob.getId());
        userJobDTO.setUserId(userJob.getUserId());
        userJobDTO.setStatus(userJob.getStatus());
        userJobDTO.setStatusChangedAt(userJob.getStatusChangedAt());

        if (user != null) {
            userJobDTO.setUserName(user.getUserName());
        }

        if (job != null) {
            JobDTO jobDTO = modelMapper.map(job, JobDTO.class);
            userJobDTO.setJob(jobDTO);
        }

        return userJobDTO;
    }

    // Helper method to validate status
    private void validateStatus(String status) {
        List<String> validStatuses = List.of("new", "applied", "interview", "offer", "rejected");
        if (!validStatuses.contains(status.toLowerCase())) {
            throw new APIException("Invalid job status. Valid statuses are: " + String.join(", ", validStatuses));
        }
    }
}