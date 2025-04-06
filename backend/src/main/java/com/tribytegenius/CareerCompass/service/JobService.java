package com.tribytegenius.CareerCompass.service;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.JobResponse;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;

import java.util.List;

public interface JobService {
    String getNewJobs(
            String keyword
    );

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
