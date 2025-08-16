package com.tribytegenius.CareerCompass.JobService.repository;

import com.tribytegenius.CareerCompass.JobService.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    
    Optional<Job> findByUrl(String url);
}
