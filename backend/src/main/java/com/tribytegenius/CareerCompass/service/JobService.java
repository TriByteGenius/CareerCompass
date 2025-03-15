package com.tribytegenius.CareerCompass.service;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;

import java.util.List;

public interface JobService {
    List<JobDTO> getAllJobs();

    JobDTO createJob(JobDTO jobDTO);

    JobDTO updateJob(Long id, JobDTO jobDTO);

    String deleteJob(Long id);

    void searchJob(SearchRequestBody searchRequestBody);
}
