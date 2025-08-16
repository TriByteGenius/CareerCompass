package com.tribytegenius.CareerCompass.JobService.controller;

import com.tribytegenius.CareerCompass.JobService.dto.JobDTO;
import com.tribytegenius.CareerCompass.JobService.dto.JobResponse;
import com.tribytegenius.CareerCompass.JobService.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.JobService.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<JobResponse> getAllJobs(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "website", required = false) String website,
            @RequestParam(name = "timeInDays", required = false) Integer timeInDays,
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "time", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "desc", required = false) String sortOrder
    ){
        JobResponse jobResponse = jobService.getAllJobs(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder,
                keyword,
                status,
                website,
                timeInDays
        );
        return new ResponseEntity<>(jobResponse, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody JobDTO jobDTO) {
        JobDTO createdJobDTO = jobService.createJob(jobDTO);
        return new ResponseEntity<>(createdJobDTO, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<String> searchJob(@RequestBody SearchRequestBody searchRequestBody) {
        jobService.searchJob(searchRequestBody);
        return new ResponseEntity<>("Search request sent to Python service", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO jobDTO) {
        JobDTO updatedJob = jobService.updateJob(id, jobDTO);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        String status = jobService.deleteJob(id);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
