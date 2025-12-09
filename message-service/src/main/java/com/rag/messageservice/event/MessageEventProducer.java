package com.rag.messageservice.event;

import com.ragchat.common.event.MessageEvent;
import com.ragchat.common.event.MessageEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageEventProducer {

    private static final String TOPIC = "message-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Sends a message created event to Kafka
     *
     * @param messageId the message ID
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendMessageCreatedEvent(UUID messageId, UUID sessionId, UUID userId) {
        MessageEvent event = new MessageEvent();
        event.setType(MessageEventType.CREATED);
        event.setMessageId(messageId);
        event.setSessionId(sessionId);
        event.setUserId(userId);
        event.setTimestamp(Instant.now());
        return sendEvent(event);
    }

    /**
     * Sends a message updated event to Kafka
     *
     * @param messageId the message ID
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendMessageUpdatedEvent(UUID messageId, UUID sessionId, UUID userId) {
        MessageEvent event = new MessageEvent();
        event.setType(MessageEventType.UPDATED);
        event.setMessageId(messageId);
        event.setSessionId(sessionId);
        event.setUserId(userId);
        event.setTimestamp(Instant.now());
        return sendEvent(event);
    }

    /**
     * Sends a message deleted event to Kafka
     *
     * @param messageId the message ID
     * @param sessionId the session ID
     * @param userId    the user ID
     * @return true if the event was sent successfully
     */
    public boolean sendMessageDeletedEvent(UUID messageId, UUID sessionId, UUID userId) {
        MessageEvent event = new MessageEvent();
        event.setType(MessageEventType.DELETED);
        event.setMessageId(messageId);
        event.setSessionId(sessionId);
        event.setUserId(userId);
        event.setTimestamp(Instant.now());
        return sendEvent(event);
    }

    private boolean sendEvent(MessageEvent event) {
        try {
            log.debug("Sending message event: {}", event);
            kafkaTemplate.send(TOPIC, event.getMessageId().toString(), event);
            log.debug("Successfully sent message event: {}", event);
            return true;
        } catch (Exception e) {
            log.error("Error sending message event: {}", event, e);
            return false;
        }
    }
}