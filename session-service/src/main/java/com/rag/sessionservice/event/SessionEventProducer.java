package com.rag.sessionservice.event;

import com.ragchat.common.event.SessionEvent;
import com.ragchat.common.event.SessionEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionEventProducer {

    private static final String TOPIC = "session-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a session created event to Kafka
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendSessionCreatedEvent(UUID sessionId, UUID userId) {
        return sendSessionEvent(SessionEventType.CREATED, sessionId, userId);
    }

    /**
     * Sends a session updated event to Kafka
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendSessionUpdatedEvent(UUID sessionId, UUID userId) {
        return sendSessionEvent(SessionEventType.UPDATED, sessionId, userId);
    }

    /**
     * Sends a session deleted event to Kafka
     *
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendSessionDeletedEvent(UUID sessionId, UUID userId) {
        return sendSessionEvent(SessionEventType.DELETED, sessionId, userId);
    }

    /**
     * Sends a session event to Kafka
     *
     * @param eventType the event type
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    private boolean sendSessionEvent(SessionEventType eventType, UUID sessionId, UUID userId) {
        try {
            SessionEvent event = new SessionEvent(eventType, sessionId, userId, OffsetDateTime.now());
            kafkaTemplate.send(TOPIC, sessionId.toString(), event);
            log.info("Sent event to topic {}: {}", TOPIC, event);
            return true;
        } catch (Exception e) {
            log.error("Error sending event to topic {}: {}", TOPIC, e.getMessage(), e);
            return false;
        }
    }
}