package com.rag.apigateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.gateway.routes[0]", matchIfMissing = true)
    public boolean disableYamlRouteDefinitionLocator() {
        return true;
    }
}