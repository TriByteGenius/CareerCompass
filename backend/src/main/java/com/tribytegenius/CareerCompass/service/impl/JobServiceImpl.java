package com.tribytegenius.CareerCompass.service.impl;

import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.exception.ResourceNotFoundException;
import com.tribytegenius.CareerCompass.model.Job;
import com.tribytegenius.CareerCompass.repository.JobRepository;
import com.tribytegenius.CareerCompass.service.JobService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(job -> modelMapper.map(job, JobDTO.class))
                .toList();
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
}
