package com.tribytegenius.CareerCompass.ApiGateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        logger.error("Gateway error occurred: ", ex);

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Determine status code based on exception type
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal server error";

        if (ex instanceof org.springframework.web.server.ResponseStatusException) {
            org.springframework.web.server.ResponseStatusException rsEx =
                    (org.springframework.web.server.ResponseStatusException) ex;
            status = HttpStatus.valueOf(rsEx.getStatusCode().value());
            message = rsEx.getReason() != null ? rsEx.getReason() : status.getReasonPhrase();
        }

        // Set response properties
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // Create error response body
        String errorResponse = createErrorResponse(status, message, exchange.getRequest().getPath().toString());

        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private String createErrorResponse(HttpStatus status, String message, String path) {
        return String.format(
                "{\n" +
                        "  \"timestamp\": \"%s\",\n" +
                        "  \"status\": %d,\n" +
                        "  \"error\": \"%s\",\n" +
                        "  \"message\": \"%s\",\n" +
                        "  \"path\": \"%s\"\n" +
                        "}",
                LocalDateTime.now().format(formatter),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}
