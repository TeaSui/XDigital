package com.rag.messageservice.event;

import com.rag.messageservice.repository.MessageRepository;
import com.ragchat.common.event.SessionEvent;
import com.ragchat.common.event.SessionEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionEventListener {

    private final MessageRepository messageRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * Listens for session events from Kafka and processes them
     *
     * @param events list of session events
     */
    @KafkaListener(topics = "session-events", groupId = "${spring.application.name}", containerFactory = "kafkaListenerContainerFactory")
    public void handleSessionEvent(@Payload List<SessionEvent> events) {
        if (events == null) {
            log.warn("Received null events list");
            return;
        }

        log.info("Received batch of {} session events", events.size());

        // Create a transaction template
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        for (SessionEvent event : events) {
            try {
                if (event == null) {
                    log.warn("Received null event in batch. Skipping.");
                    continue;
                }

                if (event.getType() == SessionEventType.DELETED) {
                    // Process each event in its own transaction
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            try {
                                log.debug("Handling session deleted event for session ID: {}", event.getSessionId());
                                messageRepository.deleteBySessionId(event.getSessionId());
                                log.debug("Deleted all messages for session ID: {}", event.getSessionId());
                            } catch (Exception e) {
                                log.error("Error within transaction for session ID: {}", event.getSessionId(), e);
                                // Mark the transaction for rollback
                                status.setRollbackOnly();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("Error processing session event: {}", event, e);
            }
        }

        log.info("Finished processing batch of {} session events", events.size());
    }
}