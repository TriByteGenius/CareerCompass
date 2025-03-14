package com.tribytegenius.CareerCompass.repository;

import com.tribytegenius.CareerCompass.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
