package com.tribytegenius.CareerCompass.JobService.service;

import com.tribytegenius.CareerCompass.JobService.dto.JobDTO;
import com.tribytegenius.CareerCompass.JobService.dto.JobResponse;
import com.tribytegenius.CareerCompass.JobService.dto.SearchRequestBody;

public interface JobService {
    JobResponse getAllJobs(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status,
            String website,
            Integer timeInDays
    );

    JobDTO createJob(JobDTO jobDTO);

    JobDTO updateJob(Long id, JobDTO jobDTO);

    String deleteJob(Long id);

    void searchJob(SearchRequestBody searchRequestBody);
}