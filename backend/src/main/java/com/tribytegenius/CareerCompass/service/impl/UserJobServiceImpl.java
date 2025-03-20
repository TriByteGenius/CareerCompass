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
import com.tribytegenius.CareerCompass.service.UserJobService;
import com.tribytegenius.CareerCompass.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserJobServiceImpl implements UserJobService {

    @Autowired
    private UserJobRepository userJobRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserJobDTO toggleFavorite(Long jobId) {
        User user = authUtil.loggedInUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        Optional<UserJob> existingUserJob = userJobRepository.findByUserIdAndJobId(user.getId(), jobId);

        if (existingUserJob.isPresent()) {
            // If it exists, remove it (unfavorite)
            userJobRepository.delete(existingUserJob.get());
            return null;
        } else {
            // If it doesn't exist, create it (favorite)
            UserJob userJob = new UserJob();
            userJob.setUser(user);
            userJob.setJob(job);
            userJob.setStatus("new"); // Default status for new favorites
            userJob.setStatusChangedAt(LocalDateTime.now());

            UserJob savedUserJob = userJobRepository.save(userJob);
            return convertToDTO(savedUserJob);
        }
    }

    @Override
    public UserJobDTO updateStatus(Long jobId, String status) {
        User user = authUtil.loggedInUser();

        UserJob userJob = userJobRepository.findByUserIdAndJobId(user.getId(), jobId)
                .orElseThrow(() -> new APIException("Job not found in user's favorites"));

        // Validate status
        validateStatus(status);

        userJob.setStatus(status);
        userJob.setStatusChangedAt(LocalDateTime.now());

        UserJob updatedUserJob = userJobRepository.save(userJob);
        return convertToDTO(updatedUserJob);
    }

    @Override
    public List<UserJobDTO> getFavoriteJobs() {
        User user = authUtil.loggedInUser();
        List<UserJob> favoriteJobs = userJobRepository.findByUserId(user.getId());

        return favoriteJobs.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<UserJobDTO> getJobsByStatus(String status) {
        // Validate status
        validateStatus(status);

        User user = authUtil.loggedInUser();
        List<UserJob> jobsWithStatus = userJobRepository.findByUserIdAndStatus(user.getId(), status);

        return jobsWithStatus.stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Helper method to convert entity to DTO
    private UserJobDTO convertToDTO(UserJob userJob) {
        if (userJob == null) {
            return null;
        }

        UserJobDTO userJobDTO = new UserJobDTO();
        userJobDTO.setId(userJob.getId());
        userJobDTO.setStatus(userJob.getStatus());
        userJobDTO.setStatusChangedAt(userJob.getStatusChangedAt());

        // Convert Job to JobDTO using ModelMapper
        JobDTO jobDTO = modelMapper.map(userJob.getJob(), JobDTO.class);
        userJobDTO.setJob(jobDTO);

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
