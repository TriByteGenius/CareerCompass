package com.tribytegenius.CareerCompass.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {
    @Value("${PYTHON_ENGINE_URL:http://localhost:8000/update}")
    private String pythonServiceUrl;

    @Bean
    @Qualifier("pythonServiceClient")
    public WebClient pythonServiceClient(WebClient.Builder builder) {
        return builder.baseUrl(pythonServiceUrl).build();
    }

}
