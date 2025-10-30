package com.tribytegenius.CareerCompass.JobService.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tribytegenius.CareerCompass.JobService.dto.ai.AIJobAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key:}")
    private String apiKey;

    public OpenAIClientService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
        this.objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public AIJobAnalysisResponse analyzeJobText(String jobText) {
        if (apiKey == null || apiKey.isBlank()) {
            // Graceful degradation for missing key
            return new AIJobAnalysisResponse(List.of(), "Unknown", List.of(), "Unknown", "Unknown");
        }

        String systemPrompt = "You are a helpful assistant that analyzes job descriptions and returns structured, concise fields.";
        String userPrompt = "Extract the following from the job description text.\n" +
                "Return JSON with fields: requiredSkills (string[]), experienceLevel (string), keyRequirements (string[]), roleType (string), difficulty (string).\n" +
                "- requiredSkills: 5-12 core technical skills or tools.\n" +
                "- experienceLevel: short phrase like 'Entry-level', '1-2 years', '3-5 years'.\n" +
                "- keyRequirements: 5-12 important non-skill requirements (e.g., 'REST API design', 'microservices', 'cloud', 'testing').\n" +
                "- roleType: concise role name guess (e.g., 'Backend Developer').\n" +
                "- difficulty: 'Junior' | 'Intermediate' | 'Senior'.\n\n" +
                "Job description:\n" + jobText;

        try {
            ChatRequestMessage system = new ChatRequestMessage("system", systemPrompt);
            ChatRequestMessage user = new ChatRequestMessage("user", userPrompt);
            ChatCompletionRequest request = new ChatCompletionRequest("gpt-3.5-turbo", List.of(system, user));

            String content = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(ex -> Mono.empty())
                    .map(body -> extractContent(body))
                    .blockOptional()
                    .orElse("{}");

            // Parse model JSON into DTO safely
            Map<String, Object> parsed = objectMapper.readValue(content, Map.class);
            return new AIJobAnalysisResponse(
                    (List<String>) parsed.getOrDefault("requiredSkills", List.of()),
                    (String) parsed.getOrDefault("experienceLevel", "Unknown"),
                    (List<String>) parsed.getOrDefault("keyRequirements", List.of()),
                    (String) parsed.getOrDefault("roleType", "Unknown"),
                    (String) parsed.getOrDefault("difficulty", "Unknown")
            );
        } catch (Exception e) {
            return new AIJobAnalysisResponse(List.of(), "Unknown", List.of(), "Unknown", "Unknown");
        }
    }

    private String extractContent(Map<?, ?> body) {
        try {
            List<?> choices = (List<?>) body.get("choices");
            if (choices == null || choices.isEmpty()) return "{}";
            Map<?, ?> first = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) first.get("message");
            Object content = message.get("content");
            return content != null ? content.toString().trim() : "{}";
        } catch (Exception e) {
            return "{}";
        }
    }

    // Minimal request models for OpenAI Chat API
    public record ChatCompletionRequest(
            String model,
            List<ChatRequestMessage> messages
    ) {}

    public record ChatRequestMessage(
            String role,
            @JsonProperty("content") String content
    ) {}
}


