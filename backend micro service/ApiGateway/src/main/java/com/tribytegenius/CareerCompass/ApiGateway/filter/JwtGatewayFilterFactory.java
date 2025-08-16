package com.tribytegenius.CareerCompass.ApiGateway.filter;

import com.tribytegenius.CareerCompass.ApiGateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtGatewayFilterFactory.class);

    @Autowired
    private JwtUtil jwtUtil;

    public JwtGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            
            // Skip JWT validation for auth endpoints
            if (path.startsWith("/auth/")) {
                logger.debug("Skipping JWT validation for auth path: {}", path);
                return chain.filter(exchange);
            }
            
            String authHeader = request.getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No valid Authorization header for path: {}", path);
                return this.unauthorized(exchange);
            }

            String token = authHeader.substring(7);
            
            if (!jwtUtil.validateJwtToken(token)) {
                logger.warn("Invalid JWT token for path: {}", path);
                return this.unauthorized(exchange);
            }

            String userEmail = jwtUtil.getEmailFromJwtToken(token);
            if (userEmail != null) {
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Email", userEmail)
                        .build();
                exchange = exchange.mutate().request(modifiedRequest).build();
                logger.debug("JWT validation successful for user: {}", userEmail);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {
        // Configuration properties if needed
    }
}
