package com.rag.apigateway.filter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    // Create logger field explicitly if Lombok is not working properly
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthenticationFilter.class);

    private final WebClient.Builder webClientBuilder;

    public AuthenticationFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = authHeader.replace("Bearer ", "");

            return validateToken(token)
                    .flatMap(userId -> {
                        // Add the user ID to the request headers
                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-User-ID", userId)
                                        .build())
                                .build();

                        return chain.filter(modifiedExchange);
                    })
                    .onErrorResume(error -> {
                        log.error("Authentication error: {}", error.getMessage());
                        return onError(exchange, "Invalid token", HttpStatus.FORBIDDEN);
                    });
        };
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "validateTokenFallback")
    private Mono<String> validateToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://auth-service/api/auth/validate?token=" + token)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(userId -> log.info("Successfully validated token for user: {}", userId));
    }

    private Mono<String> validateTokenFallback(String token, Throwable t) {
        log.error("Auth service is down. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Auth service is not available"));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties can go here
    }
}