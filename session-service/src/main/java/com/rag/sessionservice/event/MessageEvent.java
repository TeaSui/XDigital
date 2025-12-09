package com.rag.sessionservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {
    private MessageEventType type;
    private UUID messageId;
    private UUID sessionId;
    private OffsetDateTime timestamp;
}