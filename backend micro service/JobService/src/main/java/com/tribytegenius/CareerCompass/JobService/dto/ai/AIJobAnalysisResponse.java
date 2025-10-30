package com.tribytegenius.CareerCompass.JobService.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIJobAnalysisResponse {
    // Key technical skills extracted from JD
    private List<String> requiredSkills;
    // Experience requirement summary (e.g., "2-3 years", "Entry-level")
    private String experienceLevel;
    // Other important requirements or keywords
    private List<String> keyRequirements;
    // A concise role type guess (e.g., "Backend Developer")
    private String roleType;
    // Optional: overall difficulty estimation ("Junior", "Intermediate", "Senior")
    private String difficulty;
}


