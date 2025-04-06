package com.tribytegenius.CareerCompass.controller;

import com.tribytegenius.CareerCompass.config.AppConstants;
import com.tribytegenius.CareerCompass.dto.JobDTO;
import com.tribytegenius.CareerCompass.dto.JobResponse;
import com.tribytegenius.CareerCompass.dto.SearchRequestBody;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.service.JobService;
import com.tribytegenius.CareerCompass.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
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

    @GetMapping("/update")
    public ResponseEntity<String> getNewJobs(
            @RequestParam(name = "keyword", required = false) String keyword
    ){
        String status = jobService.getNewJobs(keyword);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody JobDTO jobDTO) {
        JobDTO createdJobDTO = jobService.createJob(jobDTO);
        return new ResponseEntity<>(createdJobDTO, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public void searchJob(@RequestBody SearchRequestBody searchRequestBody) {
        jobService.searchJob(searchRequestBody);
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
