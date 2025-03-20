package com.tribytegenius.CareerCompass.service;

import com.tribytegenius.CareerCompass.dto.UserJobDTO;
import java.util.List;

public interface UserJobService {
    UserJobDTO toggleFavorite(Long jobId);

    UserJobDTO updateStatus(Long jobId, String status);

    List<UserJobDTO> getFavoriteJobs();

    List<UserJobDTO> getJobsByStatus(String status);
}