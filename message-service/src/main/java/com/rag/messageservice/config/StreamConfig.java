package com.rag.messageservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

/**
 * This configuration class now contains only general message converter configuration.
 * Session event handling has been moved to {@link com.rag.messageservice.event.SessionEventListener}
 * using standard Kafka listeners.
 */
@Configuration
@Slf4j
public class StreamConfig {

    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setStrictContentTypeMatch(false);
        return converter;
    }
}