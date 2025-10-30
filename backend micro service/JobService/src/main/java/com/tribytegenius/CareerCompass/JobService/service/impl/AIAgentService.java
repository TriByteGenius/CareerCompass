package com.tribytegenius.CareerCompass.JobService.service.impl;

import com.tribytegenius.CareerCompass.JobService.dto.ai.AIJobAnalysisResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Service
public class AIAgentService {

    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final PageFetchService pageFetchService;
    private final OpenAIClientService openAIClientService;
    private final StringRedisTemplate redisTemplate;

    public AIAgentService(PageFetchService pageFetchService,
                          OpenAIClientService openAIClientService,
                          StringRedisTemplate redisTemplate) {
        this.pageFetchService = pageFetchService;
        this.openAIClientService = openAIClientService;
        this.redisTemplate = redisTemplate;
    }

    public AIJobAnalysisResponse analyzeJobByUrl(String url) {
        String key = cacheKey(url);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null && !cached.isBlank()) {
            return Jsons.fromJson(cached, AIJobAnalysisResponse.class);
        }

        String text = pageFetchService.fetchVisibleText(url);
        AIJobAnalysisResponse result = openAIClientService.analyzeJobText(text);
        redisTemplate.opsForValue().set(key, Jsons.toJson(result), CACHE_TTL);
        return result;
    }

    private String cacheKey(String url) {
        return "ai:analysis:" + sha256(url);
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    // Minimal JSON helper to avoid extra deps
    static class Jsons {
        private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();

        static String toJson(Object obj) {
            try { return MAPPER.writeValueAsString(obj); } catch (Exception e) { return "{}"; }
        }

        static <T> T fromJson(String json, Class<T> t) {
            try { return MAPPER.readValue(json, t); } catch (Exception e) { throw new RuntimeException(e); }
        }
    }
}


