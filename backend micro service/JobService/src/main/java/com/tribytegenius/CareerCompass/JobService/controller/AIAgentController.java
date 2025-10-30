package com.tribytegenius.CareerCompass.JobService.controller;

import com.tribytegenius.CareerCompass.JobService.dto.ai.AIAnalyzeUrlRequest;
import com.tribytegenius.CareerCompass.JobService.dto.ai.AIJobAnalysisResponse;
import com.tribytegenius.CareerCompass.JobService.service.impl.AIAgentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs/ai")
public class AIAgentController {

    private final AIAgentService aiAgentService;

    public AIAgentController(AIAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/analyze-url")
    public ResponseEntity<AIJobAnalysisResponse> analyzeByUrl(@Valid @RequestBody AIAnalyzeUrlRequest request) {
        AIJobAnalysisResponse result = aiAgentService.analyzeJobByUrl(request.getUrl());
        return ResponseEntity.ok(result);
    }
}


