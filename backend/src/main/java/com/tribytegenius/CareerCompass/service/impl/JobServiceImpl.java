package com.tribytegenius.CareerCompass.service.impl;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.JobResponse;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.exception.ResourceNotFoundException;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import com.tribytegenius.CareerCompass.service.JobService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import reactor.core.publisher.Mono;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    @Qualifier("pythonServiceClient")
    private WebClient pythonServiceClient;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobResponse getAllJobs(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status,
            String website,
            Integer timeInDays
    ) {
        // Create sort object based on sort direction and field
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create Pageable instance for pagination
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Create a base Specification to build up query
        Specification<Job> spec = Specification.where(null);

        // Add keyword filter if provided (searches in name and company)
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), "%" + keyword.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), "%" + keyword.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + keyword.toLowerCase() + "%")
                    )
            );
        }

        // Add status filter (exact match)
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status)
            );
        }

        // Add website filter (exact match)
        if (website != null && !website.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("website"), website)
            );
        }

        // Add time filter (jobs posted within X days)
        if (timeInDays != null && timeInDays > 0) {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(timeInDays);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("time"), cutoffDate)
            );
        }

        // Execute the query with all filters
        Page<Job> pageJobs = jobRepository.findAll(spec, pageable);

        // Get content from page object and convert to DTOs
        List<JobDTO> jobDTOs = pageJobs.getContent().stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .toList();

        // Create and populate the response object
        JobResponse jobResponse = new JobResponse();
        jobResponse.setContent(jobDTOs);
        jobResponse.setPageNumber(pageJobs.getNumber());
        jobResponse.setPageSize(pageJobs.getSize());
        jobResponse.setTotalElements(pageJobs.getTotalElements());
        jobResponse.setTotalPages(pageJobs.getTotalPages());
        jobResponse.setLastPage(pageJobs.isLast());

        return jobResponse;
    }

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = modelMapper.map(jobDTO, Job.class);
        job.setTime(LocalDateTime.now());
        Job savedJob = jobRepository.save(job);
        return modelMapper.map(savedJob, JobDTO.class);
    }

    @Override
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

        // Update fields from DTO but keep the original time
        existingJob.setCompany(jobDTO.getCompany());
        existingJob.setName(jobDTO.getName());
        existingJob.setStatus(jobDTO.getStatus());
        existingJob.setType(jobDTO.getType());
        existingJob.setLocation(jobDTO.getLocation());
        existingJob.setUrl(jobDTO.getUrl());
        existingJob.setWebsite(jobDTO.getWebsite());

        Job updatedJob = jobRepository.save(existingJob);
        return modelMapper.map(updatedJob, JobDTO.class);
    }

    @Override
    public String deleteJob(Long id) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
        jobRepository.delete(existingJob);
        return "Job deleted";
    }

    @Override
    public void searchJob(SearchRequestBody searchRequestBody) {
        pythonServiceClient.post()
            .bodyValue(searchRequestBody)
            .retrieve()
            .bodyToMono(Void.class)
            .onErrorResume(e -> {
                return Mono.error(new RuntimeException("Failed to call pythonServiceClient: " + e.getMessage()));
            });
    }
}
