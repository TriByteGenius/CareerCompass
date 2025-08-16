package com.tribytegenius.CareerCompass.UserJobService.service;

import com.tribytegenius.CareerCompass.UserJobService.dto.UserJobDTO;
import java.util.List;

public interface UserJobService {
    UserJobDTO toggleFavorite(Long userId, Long jobId);
    UserJobDTO updateStatus(Long userId, Long jobId, String status);
    List<UserJobDTO> getFavoriteJobs(Long userId);
    List<UserJobDTO> getJobsByStatus(Long userId, String status);
}
