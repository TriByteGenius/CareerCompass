package com.tribytegenius.CareerCompass.UserJobService.repository;

import com.tribytegenius.CareerCompass.UserJobService.model.UserJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJobRepository extends JpaRepository<UserJob, Long> {
    Optional<UserJob> findByUserIdAndJobId(Long userId, Long jobId);
    List<UserJob> findByUserId(Long userId);
    List<UserJob> findByUserIdAndStatus(Long userId, String status);
    List<UserJob> findByJobId(Long jobId);
    void deleteByUserId(Long userId);
    void deleteByJobId(Long jobId);
}
