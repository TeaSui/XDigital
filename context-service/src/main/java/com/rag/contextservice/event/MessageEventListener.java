package com.rag.contextservice.event;

import com.rag.contextservice.repository.ContextEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventListener {

    private final ContextEntryRepository contextEntryRepository;

    @KafkaListener(topics = "message-events", groupId = "${spring.application.name}", containerFactory = "messageKafkaListenerContainerFactory")
    @Transactional
    public void handleMessageEvent(List<MessageEvent> events) {
        log.info("Received batch of {} message events", events.size());

        for (MessageEvent event : events) {
            try {
                if (event.getType() == MessageEventType.DELETED) {
                    log.debug("Handling message deleted event for message ID: {}", event.getMessageId());
                    contextEntryRepository.deleteByMessageId(event.getMessageId());
                    log.debug("Deleted context entries for message ID: {}", event.getMessageId());
                }
            } catch (Exception e) {
                log.error("Error processing message event: {}", event, e);
            }
        }

        log.info("Finished processing batch of {} message events", events.size());
    }
}