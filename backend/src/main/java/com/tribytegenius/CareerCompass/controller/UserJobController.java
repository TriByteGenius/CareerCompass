package com.tribytegenius.CareerCompass.controller;

import com.tribytegenius.CareerCompass.dto.UserJobDTO;
import com.tribytegenius.CareerCompass.security.dto.MessageResponse;
import com.tribytegenius.CareerCompass.service.UserJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class UserJobController {

    @Autowired
    private UserJobService userJobService;

    @PostMapping("/{jobId}/toggle")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long jobId) {
        UserJobDTO result = userJobService.toggleFavorite(jobId);

        if (result == null) {
            return new ResponseEntity<>(new MessageResponse("Job removed from favorites"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{jobId}/status")
    public ResponseEntity<UserJobDTO> updateStatus(
            @PathVariable Long jobId,
            @RequestParam String status) {
        UserJobDTO updatedJob = userJobService.updateStatus(jobId, status);
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping
    public ResponseEntity<List<UserJobDTO>> getFavoriteJobs() {
        List<UserJobDTO> favoriteJobs = userJobService.getFavoriteJobs();
        return ResponseEntity.ok(favoriteJobs);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserJobDTO>> getJobsByStatus(@PathVariable String status) {
        List<UserJobDTO> jobs = userJobService.getJobsByStatus(status);
        return ResponseEntity.ok(jobs);
    }
}
