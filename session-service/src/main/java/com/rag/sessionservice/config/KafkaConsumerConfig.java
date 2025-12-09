package com.rag.sessionservice.config;

import com.rag.sessionservice.event.MessageEvent;
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
    public ConsumerFactory<String, MessageEvent> consumerFactory() {
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
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.rag.sessionservice.event,com.rag.messageservice.event,com.ragchat.common.event");
        // Add type mapping for improved compatibility
        props.put(JsonDeserializer.TYPE_MAPPINGS, "com.ragchat.common.event.MessageEvent:com.rag.sessionservice.event.MessageEvent");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
}