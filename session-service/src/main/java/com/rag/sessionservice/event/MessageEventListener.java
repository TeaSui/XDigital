package com.rag.sessionservice.event;

import com.rag.sessionservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

    private final SessionService sessionService;

    @KafkaListener(topics = "message-events", groupId = "${spring.application.name}", containerFactory = "kafkaListenerContainerFactory")
    public void handleMessageEvent(@Payload List<MessageEvent> events) {
        if (events == null) {
            log.warn("Received null events list");
            return;
        }

        log.info("Received batch of {} message events", events.size());

        for (MessageEvent event : events) {
            try {
                if (event == null) {
                    log.warn("Received null event in batch. Skipping.");
                    continue;
                }

                if (event.getType() == MessageEventType.CREATED) {
                    log.info("Handling message created event for session ID: {}", event.getSessionId());

                    try {
                        sessionService.incrementMessageCount(event.getSessionId(), event.getTimestamp());
                    } catch (Exception e) {
                        log.warn("Converting timestamp for session ID: {}", event.getSessionId());
                        sessionService.incrementMessageCount(event.getSessionId(), OffsetDateTime.now());
                    }
                }
            } catch (Exception e) {
                log.error("Error processing message event: {}", event, e);
            }
        }

        log.info("Finished processing batch of {} message events", events.size());
    }
}