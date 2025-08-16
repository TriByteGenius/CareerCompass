package com.tribytegenius.CareerCompass.UserJobService.repository;

import com.tribytegenius.CareerCompass.UserJobService.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
