package com.rag.apigateway.config;

import com.rag.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic route configuration for the API Gateway
 * This configuration class defines routes programmatically rather than using application.yml
 * to avoid environment variable interpolation issues with the RewritePath filter
 */
@Configuration
public class RouteConfig {

    @Autowired
    private AuthenticationFilter authFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Route - with v1 prefix
                .route("auth-service-v1", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/auth/(?<segment>.*)", "/api/auth/${segment}")
                        )
                        .uri("lb://auth-service")
                )

                // Auth Service Route - direct path without v1 prefix
                .route("auth-service-direct", r -> r
                        .path("/api/auth/**")
                        .uri("lb://auth-service")
                )

                // Session Service Route - handles all session paths including root
                .route("session-service", r -> r
                        .path("/api/sessions/**", "/api/sessions")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                        )
                        .uri("lb://session-service")
                )

                // Message Service Route
                .route("message-service", r -> r
                        .path("/api/v1/sessions/*/messages/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/sessions/(?<sessionId>[^/]+)/messages/(?<segment>.*)",
                                        "/messages/${sessionId}/${segment}")
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                        )
                        .uri("lb://message-service")
                )

                // Context Service Route
                .route("context-service", r -> r
                        .path("/api/v1/messages/*/context/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/messages/(?<messageId>[^/]+)/context/(?<segment>.*)",
                                        "/context/${messageId}/${segment}")
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                        )
                        .uri("lb://context-service")
                )
                .build();
    }
}