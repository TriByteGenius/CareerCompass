package com.tribytegenius.CareerCompass.UserJobService.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${python-service.url}")
    private String pythonServiceUrl;

    @Bean
    @Qualifier("pythonServiceClient")
    public WebClient pythonServiceClient(WebClient.Builder builder) {
        return builder.baseUrl(pythonServiceUrl + "/update").build();
    }
}
