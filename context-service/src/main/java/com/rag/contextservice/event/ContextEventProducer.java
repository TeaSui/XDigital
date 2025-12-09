package com.rag.contextservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextEventProducer {

    private static final String TOPIC = "context-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a context entry created event to Kafka
     *
     * @param contextEntryId the context entry ID
     * @param sessionId the session ID
     * @param userId the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendContextCreatedEvent(UUID contextEntryId, UUID sessionId, UUID userId) {
        return sendContextEvent(ContextEventType.CREATED, contextEntryId, sessionId, userId);
    }

    /**
     * Sends a context entry updated event to Kafka
     *
     * @param contextEntryId the context entry ID
     * @param sessionId the session ID
     * @param userId the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendContextUpdatedEvent(UUID contextEntryId, UUID sessionId, UUID userId) {
        return sendContextEvent(ContextEventType.UPDATED, contextEntryId, sessionId, userId);
    }

    /**
     * Sends a context entry deleted event to Kafka
     *
     * @param contextEntryId the context entry ID
     * @param sessionId the session ID
     * @param userId the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendContextDeletedEvent(UUID contextEntryId, UUID sessionId, UUID userId) {
        return sendContextEvent(ContextEventType.DELETED, contextEntryId, sessionId, userId);
    }

    /**
     * Sends a context event to Kafka
     *
     * @param eventType the event type
     * @param contextEntryId the context entry ID
     * @param sessionId the session ID
     * @param userId the user ID
     * @return true if the event was sent successfully
     */
    private boolean sendContextEvent(ContextEventType eventType, UUID contextEntryId, UUID sessionId, UUID userId) {
        try {
            ContextEvent event = new ContextEvent(eventType, contextEntryId, sessionId, userId, OffsetDateTime.now());
            kafkaTemplate.send(TOPIC, contextEntryId.toString(), event);
            log.info("Sent event to topic {}: {}", TOPIC, event);
            return true;
        } catch (Exception e) {
            log.error("Error sending event to topic {}: {}", TOPIC, e.getMessage(), e);
            return false;
        }
    }
}