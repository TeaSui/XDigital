package com.rag.contextservice.config;

import com.rag.contextservice.event.MessageEvent;
import com.rag.contextservice.event.SessionEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, MessageEvent> messageEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(GROUP_ID_CONFIG, groupId);
        props.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // Set batch processing properties
        props.put(MAX_POLL_RECORDS_CONFIG, 500);
        props.put(FETCH_MIN_BYTES_CONFIG, 1024);
        props.put(FETCH_MAX_WAIT_MS_CONFIG, 500);

        // Use ErrorHandlingDeserializer for both key and value deserializers
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Set the delegate deserializers for ErrorHandlingDeserializer
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Configure JsonDeserializer
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MessageEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.rag.contextservice.event,com.rag.messageservice.event");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageEvent> messageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageEventConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SessionEvent> sessionEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(GROUP_ID_CONFIG, groupId);
        props.put(AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // Set batch processing properties
        props.put(MAX_POLL_RECORDS_CONFIG, 500);
        props.put(FETCH_MIN_BYTES_CONFIG, 1024);
        props.put(FETCH_MAX_WAIT_MS_CONFIG, 500);

        // Use ErrorHandlingDeserializer for both key and value deserializers
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Set the delegate deserializers for ErrorHandlingDeserializer
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Configure JsonDeserializer
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SessionEvent.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ragchat.common.event,com.rag.sessionservice.event");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SessionEvent> sessionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SessionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sessionEventConsumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
}