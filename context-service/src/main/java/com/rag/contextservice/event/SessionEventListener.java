package com.rag.contextservice.event;

import com.rag.contextservice.repository.ContextEntryRepository;
import com.ragchat.common.event.SessionEvent;
import com.ragchat.common.event.SessionEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionEventListener {

    private final ContextEntryRepository contextEntryRepository;

    @KafkaListener(topics = "session-events", groupId = "${spring.application.name}", containerFactory = "sessionKafkaListenerContainerFactory")
    @Transactional
    public void handleSessionEvent(List<SessionEvent> events) {
        log.info("Received batch of {} session events", events.size());

        for (SessionEvent event : events) {
            try {
                if (event.getType() == SessionEventType.DELETED) {
                    log.debug("Handling session deleted event for session ID: {}", event.getSessionId());
                    contextEntryRepository.deleteBySessionId(event.getSessionId());
                    log.debug("Deleted all context entries for session ID: {}", event.getSessionId());
                }
            } catch (Exception e) {
                log.error("Error processing session event: {}", event, e);
            }
        }

        log.info("Finished processing batch of {} session events", events.size());
    }
}