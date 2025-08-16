package com.tribytegenius.CareerCompass.UserJobService.controller;

import com.tribytegenius.CareerCompass.UserJobService.dto.MessageResponse;
import com.tribytegenius.CareerCompass.UserJobService.dto.UserJobDTO;
import com.tribytegenius.CareerCompass.UserJobService.service.UserJobService;
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
    public ResponseEntity<?> toggleFavorite(
            @PathVariable Long jobId,
            @RequestHeader("X-User-Email") String userEmail) {

        // Extract user ID from email header (simplified - in real app you'd decode JWT)
        Long userId = extractUserIdFromEmail(userEmail);

        UserJobDTO result = userJobService.toggleFavorite(userId, jobId);

        if (result == null) {
            return new ResponseEntity<>(new MessageResponse("Job removed from favorites"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{jobId}/status")
    public ResponseEntity<UserJobDTO> updateStatus(
            @PathVariable Long jobId,
            @RequestParam String status,
            @RequestHeader("X-User-Email") String userEmail) {

        Long userId = extractUserIdFromEmail(userEmail);
        UserJobDTO updatedJob = userJobService.updateStatus(userId, jobId, status);
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping
    public ResponseEntity<List<UserJobDTO>> getFavoriteJobs(
            @RequestHeader("X-User-Email") String userEmail) {

        Long userId = extractUserIdFromEmail(userEmail);
        List<UserJobDTO> favoriteJobs = userJobService.getFavoriteJobs(userId);
        return ResponseEntity.ok(favoriteJobs);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserJobDTO>> getJobsByStatus(
            @PathVariable String status,
            @RequestHeader("X-User-Email") String userEmail) {

        Long userId = extractUserIdFromEmail(userEmail);
        List<UserJobDTO> jobs = userJobService.getJobsByStatus(userId, status);
        return ResponseEntity.ok(jobs);
    }

    // Simplified method to extract user ID from email
    // In a real application, you would decode the JWT token or call user service
    private Long extractUserIdFromEmail(String email) {
        // This is a simplified implementation
        // In production, you would either:
        // 1. Call the user service to get user ID by email
        // 2. Extract user ID from JWT token
        // 3. Use a user cache/lookup service

        // For now, using a simple hash-based approach for demo
        return (long) Math.abs(email.hashCode() % 1000000);
    }
}