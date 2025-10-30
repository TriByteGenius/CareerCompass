package com.tribytegenius.CareerCompass.JobService.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalyzeUrlRequest {
    @NotBlank
    private String url;
}


